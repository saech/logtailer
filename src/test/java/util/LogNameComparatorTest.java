package util;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LogNameComparatorTest {
    LogNameComparator comparator = new LogNameComparator("access.log.", "access.log");
    private List<File> files;

    @Test
    public void compare() throws Exception {
        files = new ArrayList<>();
        String first = "access.log.1";
        files.add(mockFile(first));
        String mainLogFile = "access.log";
        files.add(mockFile(mainLogFile));
        String second = "access.log.2";
        files.add(mockFile(second));
        String last = "access.log.10";
        files.add(mockFile(last));
        files.sort(comparator);
        Assert.assertEquals(last, getName(0));
        Assert.assertEquals(second, getName(1));
        Assert.assertEquals(first, getName(2));
        Assert.assertEquals(mainLogFile, getName(3));
    }

    private String getName(int index) {
        return files.get(index).getName();
    }

    private File mockFile(String desiredName) {
        File mock = Mockito.mock(File.class);
        Mockito.when(mock.getName()).thenReturn(desiredName);
        return mock;
    }
}