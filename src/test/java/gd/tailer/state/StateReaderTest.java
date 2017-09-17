package gd.tailer.state;

import org.junit.Assert;
import org.junit.Test;
import gd.tailer.TempDirTest;

import java.io.IOException;

public class StateReaderTest extends TempDirTest {
    @Test
    public void restoreStateTest() throws Exception {
        test("/states/state1.txt", 4, 7777, 15999);
    }

    @Test
    public void restoreStatePartialWriteTest() throws Exception {
        test("/states/state2.txt", 0, 0, 0);
    }

    @Test
    public void restoreStateEmptyTest() throws Exception {
        test("/states/state3.txt", 0, 0, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void restoreStateNoFileTest() throws Exception {
        StateReader stateReader = new StateReader(dir.resolve("nosuchfilepresent").toFile());
        stateReader.restoreState();
    }

    private void test(String resourcePath, long expectedInode, long expectedLocalOffset, long expectedGlobalOffset) throws IOException {
        String tempFilename = "tmp";
        copyResourceToTempDir(resourcePath, tempFilename);
        StateReader stateReader = new StateReader(dir.resolve(tempFilename).toFile());
        State state = stateReader.restoreState();
        Assert.assertEquals(expectedInode, state.getInode());
        Assert.assertEquals(expectedLocalOffset, state.getLocalOffset());
        Assert.assertEquals(expectedGlobalOffset, state.getGlobalOffset());
    }
}