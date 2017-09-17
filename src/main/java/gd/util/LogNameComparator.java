package gd.util;

import java.io.File;
import java.util.Comparator;

public class LogNameComparator implements Comparator<File> {
    private final String prefix;
    private final String greatest;

    public LogNameComparator(String rotatedPrefix, String mainLogName) {
        this.prefix = rotatedPrefix;
        this.greatest = mainLogName;
    }
    @Override
    public int compare(File o1, File o2) {
        String first = o1.getName();
        String second = o2.getName();

        //Check if mainLog
        if (first.equals(greatest)) {
            return 1;
        }
        if (second.equals(greatest)) {
            return -1;
        }

        Integer firstIndex = getIndex(first);
        Integer secondIndex = getIndex(second);
        if (firstIndex == null || secondIndex == null) {
            throw new IllegalArgumentException("illegal file name is applicable for given mask");
        }

        return firstIndex.compareTo(secondIndex);
    }

    private Integer getIndex(String name) {
        return Integer.valueOf(name.substring(prefix.length()));
    }
}
