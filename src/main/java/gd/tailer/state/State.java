package gd.tailer.state;

public class State {
    public static final char SEPARATOR = '\t';

    private long pos;
    private long inode;

    public State() {
    }

    public State(long pos, long inode) {
        this.pos = pos;
        this.inode = inode;
    }

    public long getPos() {
        return pos;
    }

    public long getInode() {
        return inode;
    }

    @Override
    public String toString() {
        return "State{" +
                "pos=" + pos +
                ", inode=" + inode +
                '}';
    }

    public String forStateFile() {
        return String.format("%s%c%s\n ", pos, SEPARATOR, inode);
    }
}
