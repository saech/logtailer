package tailer;

import tailer.state.State;

import java.io.IOException;

public interface RotatingReader {
    /**
     * inits reader with initial state - last iNode & pos acked
     * @param state - instance of State class which corresponds to last ACKed state
     * @throws IOException
     */
    void init(State state) throws IOException;
    int read(byte[] bytes) throws IOException;
}
