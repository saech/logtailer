package gd.util;

import java.io.File;
import java.io.FilenameFilter;

public class RotatedFilesFilter implements FilenameFilter {
    private final String prefix;

    public RotatedFilesFilter(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.startsWith(prefix) && name.substring(prefix.length() - 1).length() > 0;
    }
}
