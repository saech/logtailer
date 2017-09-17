package gd.context;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ConfigContext {
    private final Config config = ConfigFactory.load();

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

    public String getHost() {
        return config.getString("tailer.sender.connection.ip");
    }

    public int getPort() {
        return config.getInt("tailer.gd.sender.connection.port");
    }
}