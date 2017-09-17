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

public class Application {
    private static final Logger log = LogManager.getLogger(Application.class);
    private static final int DELAY = 1000;

    private final ConfigContext ctx;

    private LogReader logReader;

    private final File statePath;
    private final OffsetMapping mapping;

    public Application(ConfigContext ctx) {
        this.ctx = ctx;
        this.statePath = new File(ctx.getStatePath());

        try {
            statePath.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("can't create state file");
        }

        File mainLog = new File(ctx.getCurrentFilename());
        RotatingFileChooser rfc = new RotatingFileChooser(
                new FileChooserImpl(new File(ctx.getLogPath()), mainLog, ctx.getRotatedPrefix()),
                mainLog
        );

        this.mapping = new OffsetMapping();
        this.logReader = new LogReader(rfc);
    }

    public static void main(String[] args) {
        log.info("initializing");
        Application app = new Application(new ConfigContext());
        try {
            app.tail();
        } catch (InterruptedException e) {
            log.error("unexpected interruption. shutting down.", e);
        }
    }

    public void tail() throws InterruptedException {
        log.debug("state path: {}", statePath.toString());
        StateReader stateReader = new StateReader(statePath);
        StateWriter stateWriter = new StateWriter(statePath);
        State state = stateReader.restoreState();

        long globalOffset = state.getGlobalOffset();
        byte[] bytes = new byte[2048];
        int num = 0;
        TcpSender tcpSender = null;

        logReader.init(state);

        while (true) {
            if (num <= 0) {
                num = logReader.read(bytes);
                if (num <= 0) {
                    log.debug("no data, falling asleep");
                    Thread.sleep(DELAY);
                    continue;
                }
            }

            if (tcpSender == null) {
                try {
                    tcpSender = new TcpSender(ctx.getHost(), ctx.getPort());
                    tcpSender.open(globalOffset);
                } catch (IOException e) {
                    log.debug("exception during tcp sender opening, falling asleep", e);
                    Thread.sleep(DELAY);
                    continue;
                }
            }

            try {
                tcpSender.write(bytes, num);
                long read = tcpSender.read();
                if (read != -1) {
                    stateWriter.writeState(mapping.getStateByGlobalOffset(read));
                }
            } catch (IOException e) {
                log.error("exception during server interactions", e);
                tcpSender.close();
                tcpSender = null;
                Thread.sleep(DELAY);
            }

            log.info("num of bytes sent: {}", num);
            globalOffset += num;
            num = 0;
            mapping.put(logReader.getInode(), globalOffset);
        }
    }
}
