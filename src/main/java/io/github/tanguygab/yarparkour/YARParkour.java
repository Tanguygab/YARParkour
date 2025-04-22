package io.github.tanguygab.yarparkour;

import io.github.tanguygab.yarparkour.config.YARPConfig;
import io.github.tanguygab.yarparkour.managers.YARPParkourManager;
import io.github.tanguygab.yarparkour.managers.YARPPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Reader;
import java.util.List;

public final class YARParkour extends JavaPlugin {

    private YARPParkourManager parkourManager;
    private YARPPlayerManager playerManager;

    private YARPCommand command;
    private YARPConfig config;
    private FileConfiguration messages;
    private YARPExpansion expansion;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        saveResource("messages.yml",false);
        Reader msgsFile = getTextResource("messages.yml");
        assert msgsFile != null;
        messages = YamlConfiguration.loadConfiguration(msgsFile);

        config = new YARPConfig(this);

        (parkourManager = new YARPParkourManager(this)).load();
        (playerManager = new YARPPlayerManager(this)).load();

        PluginCommand command = getCommand("yarparkour");
        if (command != null) {
            command.setExecutor(this.command = new YARPCommand(this));
            command.setTabCompleter(this.command);
        }

        getServer().getPluginManager().registerEvents(new YARPListener(this), this);
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI"))
            (expansion = new YARPExpansion(this)).register();
    }

    @Override
    public void onDisable() {
        if (expansion != null) {
            expansion.unregister();
            expansion = null;
        }
        playerManager.unload();
        parkourManager.unload();
        HandlerList.unregisterAll(this);
    }

    public YARPParkourManager getParkourManager() {
        return parkourManager;
    }

    public YARPPlayerManager getPlayerManager() {
        return playerManager;
    }

    public YARPCommand getCommand() {
        return command;
    }

    public YARPConfig getConfiguration() {
        return config;
    }

    public String getMessage(String path) {
        return messages.getString(path, "&4Missing \"&c" + path + "&4\" in messages.yml");
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public List<String> getMessages(String path) {
        return messages.getStringList(path);
    }

}
