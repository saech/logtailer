package gd.tailer.filechooser;

public interface FileChooser {
    public FileNode findNext(long inode);

    public FileNode findOldest();

    public FileNode find(long inode);

    public boolean isHead(FileNode fn);
}
