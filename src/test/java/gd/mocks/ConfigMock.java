package gd.mocks;

import gd.context.ConfigContext;
import org.mockito.Mockito;

import java.nio.file.Path;

public class ConfigMock {
    private ConfigContext ctxMock = Mockito.spy(ConfigContext.class);

    public static int getPort() {
        return 61777;
    }

    public ConfigMock mockHost(String host) {
        Mockito.when(ctxMock.getHost()).thenReturn(host);
        return this;
    }

    public ConfigMock mockPort(int port) {
        Mockito.when(ctxMock.getPort()).thenReturn(port);
        return this;
    }

    public ConfigMock mockDirectory(Path path) {
        Mockito.when(ctxMock.getLogPath()).thenReturn(path.toString());
        return this;
    }

    public ConfigMock mockStatePath(Path path) {
        Mockito.when(ctxMock.getStatePath()).thenReturn(path.toString());
        return this;
    }

    public ConfigMock mockMainLog(Path path) {
        Mockito.when(ctxMock.getCurrentFilename()).thenReturn(path.getFileName().toString());
        return this;
    }

    public ConfigMock mockMask(String mask) {
        Mockito.when(ctxMock.getRotatedPrefix()).thenReturn(mask);
        return this;
    }

    public ConfigContext getConfigContext() {
        return ctxMock;
    }

    public static ConfigContext getLocalConfig(Path path) {
        return new ConfigMock().mockDirectory(path)
                .mockHost("localhost")
                .mockPort(getPort())
                .mockMainLog(path.resolve("log.txt"))
                .mockMask("log.")
                .mockStatePath(path.resolve("state.txt"))
                .getConfigContext();
    }
}
