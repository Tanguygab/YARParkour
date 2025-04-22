package io.github.tanguygab.yarparkour.subcommands;

import io.github.tanguygab.yarparkour.YARParkour;
import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpCommand extends SubCommand {

    public HelpCommand(YARParkour plugin) {
        super(plugin, "help");
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {
        List<String> msg = plugin.getMessages("commands.help.message");
        List<String> commandMsg = plugin.getMessages("commands.help.command");

        for (String subcommand : plugin.getCommand().getSubcommands()) {
            String usage = plugin.getMessage("commands." + subcommand + ".usage");
            String description = plugin.getMessage("commands." + subcommand + ".description");
            for (String cmdMsg : commandMsg) {
                msg.add(cmdMsg
                        .replace("{usage}", usage)
                        .replace("{description}", description)
                );
            }
        }
        sendMessage(sender, String.join("\n", msg).replace("{command}", command));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String[] args) {
        return List.of();
    }
}
