package gd.tailer.filechooser;

public interface FileChooser {
    FileNode findNext(long inode);

    FileNode findOldest();

    FileNode find(long inode);

    boolean isHead(FileNode fn);
}
