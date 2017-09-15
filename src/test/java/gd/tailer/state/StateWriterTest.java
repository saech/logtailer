package gd.tailer.state;

import gd.tailer.TempDirTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;

public class StateWriterTest extends TempDirTest {
    @Test
    public void writeReadTest() throws IOException {
        Path p = dir.resolve("state.txt");
        StateWriter w = new StateWriter(p.toFile());
        StateReader r = new StateReader(p.toFile());

        w.writeState(new State(10000, 777));
        w.writeState(new State(2222, 888));

        String content = readFileToString(p);
        System.out.println(content);

        State state = r.restoreState();
        Assert.assertEquals(2222, state.getPos());
        Assert.assertEquals(888, state.getInode());
    }
}