package io.github.tanguygab.yarparkour.config;

import io.github.tanguygab.yarparkour.YARParkour;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class YARPConfig {

    private final Map<YARPCheckpointType, YARPCheckpointConfig> checkpoints;
    private final double distance;

    public YARPConfig(YARParkour plugin) {
        ConfigurationSection config = plugin.getConfig();

        distance = config.getDouble("distance", 1.5);

        checkpoints = Map.of(
                YARPCheckpointType.START, getCheckpointConfig(config, "start"),
                YARPCheckpointType.CHECKPOINT, getCheckpointConfig(config, "checkpoint"),
                YARPCheckpointType.END, getCheckpointConfig(config, "end")
        );
    }

    public double getDistance() {
        return distance;
    }

    private YARPCheckpointConfig getCheckpointConfig(ConfigurationSection config, String name) {
        return new YARPCheckpointConfig(config.getConfigurationSection("checkpoints." + name));
    }

    public YARPCheckpointConfig getCheckpoint(YARPCheckpointType type) {
        return checkpoints.get(type);
    }

}
