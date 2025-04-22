package io.github.tanguygab.yarparkour.subcommands.admin;

import io.github.tanguygab.yarparkour.YARParkour;
import io.github.tanguygab.yarparkour.subcommands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DeleteCommand extends SubCommand {

    public DeleteCommand(YARParkour plugin) {
        super(plugin, "delete", true);
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, getMessage("commands.deleted.no-name"));
            return;
        }

        String name = args[0];
        if (plugin.getParkourManager().getParkour(name) == null) {
            sendMessage(sender, getMessage("commands.invalid.parkour"));
            return;
        }

        plugin.getParkourManager().deleteParkour(name);
        sendMessage(sender, getMessage("commands.delete.deleted"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String[] args) {
        return plugin.getParkourManager().getParkours();
    }
}
