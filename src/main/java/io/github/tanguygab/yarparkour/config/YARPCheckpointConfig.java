package io.github.tanguygab.yarparkour.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class YARPCheckpointConfig {

    private final Material block;
    private final String text;

    protected YARPCheckpointConfig(ConfigurationSection config) {
        if (config == null) {
            block = Material.GOLD_BLOCK;
            text = "&4No text configured.";
            return;
        }
        block = Material.getMaterial(config.getString("block", "GOLD_BLOCK"));
        text = String.join("\n", config.getStringList("text"));
    }

    public Material getBlock() {
        return block;
    }

    public String getText() {
        return text;
    }
}