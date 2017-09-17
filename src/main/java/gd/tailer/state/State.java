package gd.tailer.state;

public class State {
    public static final char SEPARATOR = '\t';

    private final long inode;
    private final long localOffset;
    private final long globalOffset;

    public State() {
        this.localOffset = 0;
        this.inode = 0;
        this.globalOffset = 0;
    }

    public State(long inode, long localOffset, long globalOffset) {
        this.localOffset = localOffset;
        this.inode = inode;
        this.globalOffset = globalOffset;
    }

    public long getLocalOffset() {
        return localOffset;
    }

    public long getGlobalOffset() {
        return globalOffset;
    }

    public long getInode() {
        return inode;
    }

    public String forStateFile() {
        return String.format("%s%c%s%c%s\n ", globalOffset, SEPARATOR, inode, SEPARATOR, localOffset);
    }
}
