package io.github.tanguygab.yarparkour.entities;

import io.github.tanguygab.yarparkour.YARParkour;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class YARManager {

    protected final YARParkour plugin;

    protected final FileConfiguration data;
    private final File dataFile;

    public YARManager(YARParkour plugin, String fileName) {
        this.plugin = plugin;
        dataFile = new File(plugin.getDataFolder(), fileName);
        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    public abstract void load();
    public abstract void unload();

    public void save() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("An error occured while saving " + dataFile.getName() + ": " + e.getMessage());
        }
    }


}
