package tailer.filechooser;

import java.io.*;

public class FileNode implements Closeable {
    private final File file;
    private final long inode;
    private final RandomAccessFile raf;

    private FileNode(File file) throws IOException {
        this.file = file;
        this.raf = new RandomAccessFile(file, "r");
        this.inode = InodeWrap.getInode(file);
    }

    public int read(byte[] bytes) throws IOException {
        return this.raf.read(bytes);
    }

    public boolean isMoved() throws IOException {
        return InodeWrap.getInode(file) != inode;
    }

    public File getFile() {
        return file;
    }

    public long getInode() {
        return inode;
    }

    @Override
    public void close() throws IOException {
        raf.close();
    }

    public void seek(long pos) throws IOException {
        raf.seek(pos);
    }

    public static FileNode open(File f) throws IOException {
        return new FileNode(f);
    }

    public long getOffset() {
        try {
            return raf.getFilePointer();
        } catch (IOException e) {
            return -1;
        }
    }
}
