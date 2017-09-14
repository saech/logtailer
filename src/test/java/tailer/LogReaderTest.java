package tailer;

import context.ConfigContext;
import org.junit.Assert;
import org.junit.Test;
import tailer.filechooser.FileChooserImpl;
import tailer.filechooser.RotatingFileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;

public class LogReaderTest extends TempDirTest {
    @Test
    public void testLogReader() throws IOException {
        copyResourceToTempDir("/logs/log.1", "log.1");
        copyResourceToTempDir("/logs/log.2", "log.2");
        copyResourceToTempDir("/logs/log.txt", "log.txt");

        Path mainLog = dir.resolve("log.txt");

        FileChooserImpl fc = new FileChooserImpl(
                dir.toFile(),
                mainLog.toFile(),
                "log."
        );
        RotatingFileChooser rfc = new RotatingFileChooser(fc, mainLog.toFile());
        LogReader logReader = new LogReader(rfc);

        ArrayList<String> strings = new ArrayList<>();

        readAll(logReader, strings);
        Assert.assertEquals(3, strings.size());
        Assert.assertEquals("lastfile", strings.get(0));
        Assert.assertEquals("midfile", strings.get(1));
        Assert.assertEquals("mainfile", strings.get(2));
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

    @Test
    public void test() throws Exception {
        ConfigContext ctx = new ConfigContext();
        File log = new File(ctx.getLogPath(), ctx.getCurrentFilename());
        FileChooserImpl fc = new FileChooserImpl(
                new File(ctx.getLogPath()),
                log,
                ctx.getRotatedPrefix()
        );

        RotatingFileChooser rfc = new RotatingFileChooser(fc, log);
        byte[] bytes = new byte[8192];
        LogReader logReader = new LogReader(rfc);
        while (true) {
            int read = logReader.read(bytes);
            if (read != -1)
                System.out.println(new String(bytes, 0, read));
        }
    }
}