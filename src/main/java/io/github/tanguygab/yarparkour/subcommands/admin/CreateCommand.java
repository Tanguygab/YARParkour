package io.github.tanguygab.yarparkour.subcommands.admin;

import io.github.tanguygab.yarparkour.YARParkour;
import io.github.tanguygab.yarparkour.subcommands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CreateCommand extends SubCommand {

    public CreateCommand(YARParkour plugin) {
        super(plugin, "create", true);
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, getMessage("commands.invalid.no-name"));
            return;
        }

        String name = args[0];
        if (name.contains(".")) {
            sendMessage(sender, getMessage("commands.invalid.name"));
            return;
        }

        if (plugin.getParkourManager().getParkour(name) != null) {
            sendMessage(sender, getMessage("commands.create.already-exists"));
            return;
        }

        plugin.getParkourManager().createParkour(name);
        sendMessage(sender, getMessage("commands.create.created").replace("{parkour}", name));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String[] args) {
        return List.of();
    }
}
