package gd.mocks;

import gd.Application;
import gd.context.ConfigContext;
import gd.tailer.TempDirTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

public class ClientMock extends TempDirTest {
    private static final Logger log = LogManager.getLogger(ClientMock.class);

    @Ignore
    @Test
    public void test() throws Exception {
        copyResourceToTempDir("/logs/log.txt", "log.txt");
        copyResourceToTempDir("/logs/log.1", "log.1");
        copyResourceToTempDir("/logs/log.2", "log.2");
        ConfigContext localConfig = ConfigMock.getLocalConfig(dir);
        Application app = new Application(localConfig);
        Thread appThread = new Thread(app);
        appThread.start();
        Thread.sleep(5000);
        appThread.interrupt();
        appThread.join();
    }
}
