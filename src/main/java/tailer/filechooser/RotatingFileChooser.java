package tailer.filechooser;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.util.function.Function;

public class RotatingFileChooser implements FileChooser {
    private final FileChooser fc;
    private final File mainLog;

    public RotatingFileChooser(FileChooser fc, File mainLog) {
        this.fc = fc;
        this.mainLog = mainLog;
    }

    @Override
    public FileNode findOldest() {
        return wrap(0, inode -> fc.findOldest());
    }

    @Override
    public FileNode findNext(long inode) {
        return wrap(inode, fc::findNext);
    }

    @Override
    public FileNode find(long inode) {
        return wrap(inode, fc::findNext);
    }

    @Override
    public boolean isHead(FileNode fn) {
        return fc.isHead(fn);
    }

    private FileNode wrap(long inode, Function<Long, FileNode> f){
        while (true) {
            DirectoryWatcher directoryWatcher = observeDirectory();
            FileNode fn = f.apply(inode);
            if (!directoryWatcher.rotationOccurred()) {
                return fn;
            } else {
                IOUtils.closeQuietly(fn);
            }
        }
    }

    public DirectoryWatcher observeDirectory() {
        DirectoryWatcher watcher = new DirectoryWatcher();
        watcher.watch(mainLog);
        return watcher;
    }


    private static class DirectoryWatcher {
        private File file;
        private long iNode;

        private void watch(File mainLog) {
            this.file = mainLog;
            this.iNode = InodeWrap.getInode(mainLog);
        }

        private boolean rotationOccurred() {
            return this.iNode != InodeWrap.getInode(file);
        }
    }
}
