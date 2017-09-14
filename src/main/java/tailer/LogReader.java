package tailer;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tailer.filechooser.FileChooser;
import tailer.filechooser.FileNode;
import tailer.state.State;

import java.io.IOException;

public class LogReader implements RotatingReader, AutoCloseable {
    private static final Logger log = LogManager.getLogger(LogReader.class);

    private final FileChooser fc;

    private long prevInode;
    private FileNode fn;


    public LogReader(FileChooser fileChooser) {
        this.fc = fileChooser;
    }

    public void init(State state) throws IOException {
        fn = fc.find(state.getInode());
        if (fn != null) {
            prevInode = fn.getInode();
            fn.seek(state.getLastAckedPos());
        } else {
            prevInode = 0;
        }
    }

    public int read(byte[] bytes) throws IOException {
        do {
            if (fn != null) {
                int num = fn.read(bytes);
                if (num == -1) {
                    if (!fn.isMoved() && fc.isHead(fn)) {
                        return -1;
                    }
                    fn.close();
                    prevInode = fn.getInode();
                    fn = null;
                } else {
                    return num;
                }
            }

            if (prevInode == 0) {
                fn = fc.findOldest();
            } else {
                fn = fc.findNext(prevInode);
            }
        } while (fn != null);
        return -1;
    }

    public long getInode() {
        if (fn == null) {
            return -1;
        }
        return fn.getInode();
    }

    public long getOffset() {
        if (fn == null) {
            return -1;
        }
        return fn.getOffset();
    }

    @Override
    public void close() throws Exception {
        IOUtils.closeQuietly(fn);
    }
}