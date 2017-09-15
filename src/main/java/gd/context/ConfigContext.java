package gd.context;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ConfigContext {
    Config config = ConfigFactory.load();

    public String getLogPath() {
        return config.getString("gd.tailer.file.directory");
    }

    public String getCurrentFilename() {
        return config.getString("gd.tailer.file.name.current");
    }

    public String getRotatedPrefix() {
        return config.getString("gd.tailer.file.name.rotated.prefix");
    }

    public String getStatePath() {
        return config.getString("gd.tailer.state.path");
    }

    public String getHost() {
        return config.getString("gd.tailer.gd.sender.connection.ip");
    }

    public int getPort() {
        return config.getInt("gd.tailer.gd.sender.connection.port");
    }
}