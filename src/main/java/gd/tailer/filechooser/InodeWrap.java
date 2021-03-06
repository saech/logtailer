package gd.tailer.filechooser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

public class InodeWrap {
    public static long getInode(File file) throws IOException {
        try {
            BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            Object fileKey = basicFileAttributes.fileKey();

            Field iNodeField = fileKey.getClass().getDeclaredField("st_ino");
            iNodeField.setAccessible(true);
            return (long) iNodeField.get(fileKey);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("access rights problems", e);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("incorrectly specified field name", e);
        }
    }
}
