package io.github.tanguygab.yarparkour.subcommands;

import io.github.tanguygab.yarparkour.YARParkour;
import org.bukkit.command.CommandSender;

import java.util.List;

public class EditCommand extends SubCommand {

    public EditCommand(YARParkour plugin) {
        super(plugin, "edit", true);
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String[] args) {
        return List.of();
    }
}
