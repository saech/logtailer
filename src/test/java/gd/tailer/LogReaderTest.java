package gd.tailer;

import org.junit.Assert;
import org.junit.Test;
import gd.tailer.filechooser.FileChooserImpl;
import gd.tailer.filechooser.RotatingFileChooser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class LogReaderTest extends TempDirTest {
    @Test
    public void testLogReaderWithRealTimeAppend() throws IOException {
        copyResourceToTempDir("/logs/log.1", "log.1");
        copyResourceToTempDir("/logs/log.2", "log.2");
        copyResourceToTempDir("/logs/log.txt", "log.txt");

        Path mainLog = dir.resolve("log.txt");

        LogReader logReader = prepare(mainLog);

        ArrayList<String> strings = new ArrayList<>();
        readAll(logReader, strings);

        Assert.assertEquals(3, strings.size());
        Assert.assertEquals("lastfile", strings.get(0));
        Assert.assertEquals("midfile", strings.get(1));
        Assert.assertEquals("mainfile", strings.get(2));

        String appendedText = "appendedText";
        try (FileWriter fw = new FileWriter(mainLog.toFile(), true); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(appendedText);
        }
        readAll(logReader, strings);
        Assert.assertEquals(4, strings.size());
        Assert.assertEquals(appendedText, strings.get(3));
    }

    @Test
    public void rotationTest() throws Exception {
        copyResourceToTempDir("/logs/log.txt", "log.txt");

        Path mainLog = dir.resolve("log.txt");

        LogReader logReader = prepare(mainLog);

        Files.move(dir.resolve("log.txt"), dir.resolve("log.1.txt"));
        copyResourceToTempDir("/logs/log.2", "log.txt");


        ArrayList<String> strings = new ArrayList<>();
        readAll(logReader, strings);

        Assert.assertEquals(2, strings.size());
        Assert.assertEquals("mainfile", strings.get(0));
        Assert.assertEquals("lastfile", strings.get(1));
    }

    private LogReader prepare(Path mainLog) {
        FileChooserImpl fc = new FileChooserImpl(dir.toFile(), mainLog.toFile(), "log.");
        RotatingFileChooser rfc = new RotatingFileChooser(fc, mainLog.toFile());
        return new LogReader(rfc);
    }

    private void readAll(LogReader logReader, ArrayList<String> strings) throws IOException {
        byte[] bytes = new byte[8192];
        while (true) {
            int read = logReader.read(bytes);
            if (read != -1) {
                strings.add(new String(bytes, 0, read, StandardCharsets.UTF_8));
            } else {
                break;
            }
        }
    }
}