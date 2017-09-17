package gd.sender;

import org.junit.Assert;
import org.junit.Test;
import gd.tailer.state.State;

public class OffsetMappingTest {
    @Test
    public void test() {
        OffsetMapping offsetMapping = new OffsetMapping();
        offsetMapping.put(1, 100);
        offsetMapping.put(1, 200);
        offsetMapping.put(1, 300);
        offsetMapping.put(2, 400);
        offsetMapping.put(2, 500);
        offsetMapping.put(2, 600);
        offsetMapping.put(3, 700);
        offsetMapping.put(3, 800);

        checkState(offsetMapping, 750, 3, 150);
        checkState(offsetMapping, 250, 1, 250);
        checkState(offsetMapping, 350, 2, 50);
        checkState(offsetMapping, 50, 1, 50);
        checkState(offsetMapping, 0, 1, 0);
    }

    private void checkState(OffsetMapping offsetMapping, int totalOffset, int expectedInode, int expectedRelativePos) {
        State stateByTotalOffset = offsetMapping.getStateByGlobalOffset(totalOffset);
        Assert.assertEquals(expectedInode, stateByTotalOffset.getInode());
        Assert.assertEquals(expectedRelativePos, stateByTotalOffset.getLocalOffset());
    }

}