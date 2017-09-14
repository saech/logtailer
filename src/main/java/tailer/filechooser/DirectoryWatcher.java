package tailer.filechooser;

import java.io.File;

public class DirectoryWatcher {
    private File file;
    private long iNode;

    public void watch(File mainLog) {
        this.file = mainLog;
        this.iNode = InodeWrap.getInode(mainLog);
    }

    public boolean rotationOccurred() {
        return this.iNode == InodeWrap.getInode(file);
    }
}
