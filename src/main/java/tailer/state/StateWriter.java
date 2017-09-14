package tailer.state;

import java.io.*;

public class StateWriter {
    static final char SEPARATOR = '\t';

    private final String statePath;

    public StateWriter(String statePath) {
        this.statePath = statePath;
    }

    public void writeState(State state) throws IOException {
        File file = new File(statePath);
        if (!file.exists()) {
            file.createNewFile();
        }

        try (FileWriter fw = new FileWriter(file.getAbsoluteFile(), true)) {
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(state.getLastAckedPos() + SEPARATOR + state.getInode() + "\n");
            }
        }

    }
}
