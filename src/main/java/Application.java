import context.ConfigContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sender.OffsetMapping;
import sender.TcpSender;
import tailer.LogReader;
import tailer.filechooser.FileChooserImpl;
import tailer.filechooser.RotatingFileChooser;
import tailer.state.State;
import tailer.state.StateReader;
import tailer.state.StateWriter;

import java.io.File;
import java.io.IOException;

public class Application {
    private static final Logger log = LogManager.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("tailer application class executed");
        ConfigContext ctx = new ConfigContext();
        StateReader stateReader = new StateReader(new File(ctx.getStatePath()));
        State state = stateReader.restoreState();
        File mainLog = new File(ctx.getCurrentFilename());
        FileChooserImpl fc = new FileChooserImpl(new File(ctx.getLogPath()), mainLog, ctx.getRotatedPrefix());
        RotatingFileChooser rfc = new RotatingFileChooser(fc, mainLog);

        int globalOffset = 0;
        byte[] bytes = new byte[2048];
        OffsetMapping mapping = new OffsetMapping();
        StateWriter stateWriter = new StateWriter(new File(ctx.getStatePath()));
        while (true) {
            try (LogReader logReader = new LogReader(rfc)) {
                logReader.init(state);
                int num = logReader.read(bytes);
                TcpSender tcpSender = new TcpSender(ctx.getHost(), ctx.getPort());
                tcpSender.open(globalOffset);
                globalOffset += num;
                mapping.put(logReader.getInode(), globalOffset);
                tcpSender.write(bytes, num);
                long read = tcpSender.read();
                if (read != -1) {
                    State ackedState = mapping.getStateByTotalOffset(read);
                    stateWriter.writeState(ackedState);
                }
            } catch (IOException e) {
                log.warn("ioexception occurred", e);
            }
        }
    }
}
