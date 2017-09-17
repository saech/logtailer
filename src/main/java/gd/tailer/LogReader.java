package gd.tailer;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import gd.tailer.filechooser.FileChooser;
import gd.tailer.filechooser.FileNode;
import gd.tailer.state.State;

import java.io.IOException;

public class LogReader implements RotatingReader, AutoCloseable {
    private static final Logger log = LogManager.getLogger(LogReader.class);

    private final FileChooser fc;

    private long prevInode;
    private FileNode fn;


    public LogReader(FileChooser fileChooser) {
        this.fc = fileChooser;
    }

    public void init(State state) {
        do {
            try {
                fn = fc.find(state.getInode());
                if (fn != null) {
                    fn.seek(state.getLocalOffset());
                    prevInode = fn.getInode();
                } else {
                    prevInode = 0;
                }
                return;
            } catch (IOException e) {
                log.error("error during log reader initialization from previous state. Try to take next inode");
                IOUtils.closeQuietly(fn);
                fn = fc.findNext(state.getInode());
            }
        } while (fn == null);
    }

    public int read(byte[] bytes) {
        do {
            if (fn != null) {
                int num;
                try {
                    num = fn.read(bytes);
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
                } catch (IOException e) {
                    log.error("exception during file reading");
                    IOUtils.closeQuietly(fn);
                    fn = null;
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

    @Override
    public void close() {
        IOUtils.closeQuietly(fn);
    }
}