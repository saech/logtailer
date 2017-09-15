package gd;

import gd.context.ConfigContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import gd.sender.OffsetMapping;
import gd.sender.TcpSender;
import gd.tailer.LogReader;
import gd.tailer.filechooser.FileChooserImpl;
import gd.tailer.filechooser.RotatingFileChooser;
import gd.tailer.state.State;
import gd.tailer.state.StateReader;
import gd.tailer.state.StateWriter;

import java.io.File;
import java.io.IOException;

public class Application implements Runnable {
    private static final Logger log = LogManager.getLogger(Application.class);
    public static final int DELAY = 1000;
    private final ConfigContext ctx;

    public Application(ConfigContext ctx) {
        this.ctx = ctx;
    }

    public Application() {
        this.ctx = new ConfigContext();
    }

    public static void main(String[] args) {
        log.info("gd.tailer application class executed");
        Application app = new Application(new ConfigContext());
        app.run();
    }

    public void run() {
        File statePath = new File(ctx.getStatePath());
        StateReader stateReader = new StateReader(statePath);
        State state = stateReader.restoreState();
        File mainLog = new File(ctx.getCurrentFilename());
        FileChooserImpl fc = new FileChooserImpl(new File(ctx.getLogPath()), mainLog, ctx.getRotatedPrefix());
        RotatingFileChooser rfc = new RotatingFileChooser(fc, mainLog);

        int globalOffset = 0;
        byte[] bytes = new byte[2048];
        OffsetMapping mapping = new OffsetMapping();
        StateWriter stateWriter = new StateWriter(statePath);
        log.info("state path: {}", statePath.toString());
        try (LogReader logReader = new LogReader(rfc)) {
            try (TcpSender tcpSender = new TcpSender(ctx.getHost(), ctx.getPort())) {
                tcpSender.open(globalOffset);
                logReader.init(state);
                while (true) {
                    int num = logReader.read(bytes);
                    if (num != -1) {
                        globalOffset += num;
                        mapping.put(logReader.getInode(), globalOffset);
                        tcpSender.write(bytes, num);
                        log.info("num of bytes sent: {}", num);
                    } else {
                        Thread.sleep(DELAY);
                    }

                    long read = tcpSender.read();
                    if (read != -1) {
                        log.info("new offset received: {}", read);
                        State ackedState = mapping.getStateByTotalOffset(read);
                        if (ackedState != null) {
                            log.info("new state acked. pos: {}, node: {}", ackedState.getPos(), ackedState.getInode());
                            stateWriter.writeState(ackedState);
                        } else {
                            log.warn("can't map offset to inode. read offset: {}. ", read);
                            log.warn("current mapping: {}", mapping.toString());
                        }
                    }
                }
            } catch (InterruptedException e) {
                log.info("interruption received, exiting.");
            }
        } catch (IOException e) {
            log.warn("ioexception occurred", e);
        }
    }
}
