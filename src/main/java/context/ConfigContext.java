package context;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ConfigContext {
    Config config = ConfigFactory.load();

    public String getLogPath() {
        return config.getString("tailer.file.directory");
    }

    public String getCurrentFilename() {
        return config.getString("tailer.file.name.current");
    }

    public String getRotatedPrefix() {
        return config.getString("tailer.file.name.rotated.prefix");
    }

    public String getStatePath() {
        return config.getString("tailer.state.path");
    }

}