package io.github.tanguygab.yarparkour.subcommands;

import io.github.tanguygab.yarparkour.YARParkour;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CreateCommand extends SubCommand {

    public CreateCommand(YARParkour plugin) {
        super(plugin, "create", true);
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String[] args) {
        return List.of();
    }
}
