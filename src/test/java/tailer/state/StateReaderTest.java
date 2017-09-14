package tailer.state;

import org.junit.Assert;
import org.junit.Test;
import tailer.TempDirTest;

import java.io.IOException;

public class StateReaderTest extends TempDirTest {
    @Test
    public void restoreStateTest() throws Exception {
        test("/states/state1.txt", 7777, 4);
    }

    @Test
    public void restoreStatePartialWriteTest() throws Exception {
        test("/states/state2.txt", 0, 0);
    }

    @Test
    public void restoreStateEmptyTest() throws Exception {
        test("/states/state3.txt", 0, 0);
    }

    @Test
    public void restoreStateNoFileTest() throws Exception {
        StateReader stateReader = new StateReader(dir.resolve("nosuchfilepresent").toString());
        State state = stateReader.restoreState();
        Assert.assertEquals(0, state.getInode());
        Assert.assertEquals(0, state.getLastAckedPos());
    }

    private void test(String resourcePath, int expectedInode, int expectedPos) throws IOException {
        String tempFilename = "tmp";
        copyResourceToTempDir(resourcePath, tempFilename);
        StateReader stateReader = new StateReader(dir.resolve(tempFilename).toString());
        State state = stateReader.restoreState();
        Assert.assertEquals(expectedInode, state.getInode());
        Assert.assertEquals(expectedPos, state.getLastAckedPos());
    }
}