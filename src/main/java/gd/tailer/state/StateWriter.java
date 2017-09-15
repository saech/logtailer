package gd.tailer.state;

import java.io.*;

public class StateWriter {
    private final File file;

    public StateWriter(File file) {
        this.file = file;
    }

    public void writeState(State state) throws IOException {
        try (FileWriter fw = new FileWriter(file, true)) {
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(state.forStateFile());
            }
        }
    }
}