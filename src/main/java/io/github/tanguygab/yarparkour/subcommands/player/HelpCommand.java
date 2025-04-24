package io.github.tanguygab.yarparkour.subcommands.player;

import io.github.tanguygab.yarparkour.YARParkour;
import io.github.tanguygab.yarparkour.subcommands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand extends SubCommand {

    private final List<String> msg = plugin.getMessages("commands.help.message");
    private final List<String> commandMsg = plugin.getMessages("commands.help.command");

    public HelpCommand(YARParkour plugin) {
        super(plugin, "help");
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {
        List<String> subcommands = new ArrayList<>();
        for (String subcommand : plugin.getCommand().getSubcommands(sender)) {
            String usage = getMessage("commands." + subcommand + ".usage");
            String description = getMessage("commands." + subcommand + ".description");
            for (String cmdMsg : commandMsg) {
                subcommands.add(cmdMsg
                        .replace("{usage}", usage)
                        .replace("{description}", description)
                );
            }
        }
        sendMessage(sender, String.join("\n", msg)
                .replace("{commands}", String.join("\n", subcommands))
                .replace("{command}", command)
                .replace("{version}", plugin.getDescription().getVersion())
        );
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String[] args) {
        return List.of();
    }
}
