package tailer.filechooser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

public class InodeWrap {
    /**
     * Uses reflection to get st_ino field from UnixFileKey
     * @param file
     * @throws IllegalArgumentException up on any fail during inode retrieval
     * @return long inode representation
     */
    public static long getInode(File file) {
        while (true) {
            try {
                BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                Object fileKey = basicFileAttributes.fileKey();

                Class<?> aClass = fileKey.getClass();
                Field iNodeField = aClass.getDeclaredField("st_ino");
                iNodeField.setAccessible(true);
                return (long) iNodeField.get(fileKey);
            } catch (IOException e) {
                //what to do? file was deleted - no need for retry?
            } catch (IllegalAccessException e) {
                //TODO: add logging
                throw new IllegalStateException("access rights problems", e);
            } catch (NoSuchFieldException e) {
                //TODO: add logging
                throw new IllegalArgumentException("incorrectly specified field name", e);
            }
        }
    }
}
