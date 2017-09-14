package tailer.state;

public class State {
    private long lastAckedPos;
    private long iNode;

    public State() {
    }

    public State(long lastAckedPos) {
        this.lastAckedPos = lastAckedPos;
    }

    public State(long lastAckedPos, long iNode) {
        this.lastAckedPos = lastAckedPos;
        this.iNode = iNode;
    }

    public long getLastAckedPos() {
        return lastAckedPos;
    }

    public long getInode() {
        return iNode;
    }

    public void setLastAckedPos(long lastAckedPos) {
        this.lastAckedPos = lastAckedPos;
    }

    public void setiNode(long iNode) {
        this.iNode = iNode;
    }

    @Override
    public String toString() {
        return "State{" +
                "lastAckedPos=" + lastAckedPos +
                ", iNode=" + iNode +
                '}';
    }
}
