package io.github.tanguygab.yarparkour.subcommands.admin;

import io.github.tanguygab.yarparkour.YARParkour;
import io.github.tanguygab.yarparkour.entities.YARPParkour;
import io.github.tanguygab.yarparkour.subcommands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DeleteCommand extends SubCommand {

    public DeleteCommand(YARParkour plugin) {
        super(plugin, "delete", true);
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {
        YARPParkour parkour = getParkour(sender, args);
        if (parkour == null) return;

        plugin.getParkourManager().deleteParkour(parkour.getName());
        sendMessage(sender, getMessage("commands.delete.deleted"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String[] args) {
        return args.length < 2 ? plugin.getParkourManager().getParkourNames() : List.of();
    }
}
