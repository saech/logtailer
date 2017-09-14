package tailer.state;

import com.google.common.base.Strings;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StateReader {
    private static final Logger log = LogManager.getLogger(StateReader.class);

    private final File file;

    public StateReader(File file) {
        this.file = file;
    }

    public State restoreState() {
        ReversedLinesFileReader reader;
        try {
            if (!file.exists()) {
                if (file.createNewFile()) {
                    return new State();
                }
            }
            reader = new ReversedLinesFileReader(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return new State();
        }

        try {
            //We take all but last because last line is either empty or unreliable (partial write)
            String lastLine = reader.readLine();
            if (lastLine == null) {
                return new State();
            }
            while (true) {
                String preLastLine = reader.readLine();
                if (!Strings.isNullOrEmpty(preLastLine)) {
                    String[] split = StringUtils.split(preLastLine, StateWriter.SEPARATOR);
                    if (split.length != 2) {
                        //corrupted line, try next
                        log.warn("state file is corrupted");
                        continue;
                    }

                    try {
                        long offset = Long.valueOf(split[0]);
                        long ts = Long.valueOf(split[1]);
                        return new State(offset, ts);
                    } catch (NumberFormatException ex) {
                        //corrupted line, try next
                        log.warn("state file is corrupted");
                        continue;
                    }
                } else {
                    return new State();
                }
            }
        } catch (IOException e) {
            return new State();
        }
    }
}
