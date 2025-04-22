package io.github.tanguygab.yarparkour.subcommands;

import io.github.tanguygab.yarparkour.YARParkour;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand extends SubCommand {

    public ReloadCommand(YARParkour plugin) {
        super(plugin, "reload", true);
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {
        plugin.onDisable();
        plugin.onEnable();
        sender.sendMessage(plugin.getMessage("commands.reload.reloaded"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String[] args) {
        return List.of();
    }
}
