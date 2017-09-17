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

        w.writeState(new State(888, 2222, 5000));
        w.writeState(new State(777, 10000, 100000));

        String content = readFileToString(p);
        System.out.println(content);

        State state = r.restoreState();
        Assert.assertEquals(10000, state.getLocalOffset());
        Assert.assertEquals(100000, state.getGlobalOffset());
        Assert.assertEquals(777, state.getInode());
    }
}