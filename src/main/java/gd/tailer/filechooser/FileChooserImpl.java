package gd.tailer.filechooser;

import gd.util.LogNameComparator;
import gd.util.RotatedFilesFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileChooserImpl implements FileChooser {
    private final File dir;
    private final File mainLog;
    private final String rotatedPrefix;

    public FileChooserImpl(File dir, File mainLog, String rotatedPrefix) {
        this.dir = dir;
        this.mainLog = mainLog;
        this.rotatedPrefix = rotatedPrefix;
    }

    public FileNode findNext(long inode) {
        List<File> logs = getSortedLogFiles();
        boolean isNext = false;
        for (File file : logs) {
            if (isNext) {
                try {
                    return FileNode.open(file);
                } catch (IOException e) {
                    return null;
                }
            }
            try {
                if (InodeWrap.getInode(file) == inode) {
                    isNext = true;
                }
            } catch (IOException e) {
                return null;
            }
        }
        return findOldest();
    }

    public FileNode findOldest() {
        List<File> logs = getSortedLogFiles();
        if (logs.isEmpty()) {
            return null;
        }
        try {
            return FileNode.open(logs.get(0));
        } catch (IOException e) {
            return null;
        }
    }

    public FileNode find(long inode) {
        List<File> logs = getSortedLogFiles();
        for (File file : logs) {
            try {
                if (InodeWrap.getInode(file) == inode) {
                    return FileNode.open(file);
                }
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    private List<File> getSortedLogFiles() {
        File[] rotatedLogFiles = dir.listFiles(new RotatedFilesFilter(rotatedPrefix));
        if (rotatedLogFiles == null) {
            return Collections.emptyList();
        }
        List<File> logs = new ArrayList<>(Arrays.asList(rotatedLogFiles));
        logs.add(mainLog);
        logs.sort(new LogNameComparator(rotatedPrefix, mainLog.getName()));
        return logs;
    }

    public boolean isHead(FileNode fn) {
        return mainLog.getName().equals(fn.getFile().getName());
    }
}
