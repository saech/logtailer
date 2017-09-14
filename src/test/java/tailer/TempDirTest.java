package tailer;

import org.junit.Before;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TempDirTest {
    protected Path dir;

    @Before
    public void setUp() throws IOException {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();
        dir = temporaryFolder.getRoot().toPath();
    }

    protected static String readFileToString(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    protected void copyResourceToTempDir(String resourcePath, String targetName) throws IOException {
        try (InputStream resStream = this.getClass().getResourceAsStream(resourcePath)) {
            Files.copy(resStream, dir.resolve(targetName));
        }
    }
}
