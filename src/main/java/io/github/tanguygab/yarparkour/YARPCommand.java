package io.github.tanguygab.yarparkour;

import io.github.tanguygab.yarparkour.subcommands.SubCommand;
import io.github.tanguygab.yarparkour.subcommands.admin.*;
import io.github.tanguygab.yarparkour.subcommands.player.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YARPCommand implements CommandExecutor, TabCompleter {

    private final Map<String, SubCommand> subcommands;

    public YARPCommand(YARParkour plugin) {
        subcommands = new HashMap<>() {{
            put("create", new CreateCommand(plugin));
            put("delete", new DeleteCommand(plugin));
            put("edit", new EditCommand(plugin));
            put("info", new InfoCommand(plugin));
            put("list", new ListCommand(plugin));
            put("reload", new ReloadCommand(plugin));

            put("checkpoint", new CheckpointCommand(plugin));
            put("help", new HelpCommand(plugin));
            put("reset", new ResetCommand(plugin));
            put("start", new StartCommand(plugin));
            put("stop", new StopCommand(plugin));
            put("top", new TopCommand(plugin));
        }};
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        getSubcommand(sender, args).onCommand(sender, label, args.length > 0 ? Arrays.copyOfRange(args, 1, args.length) : args);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return args.length > 1
                ? getSubcommand(sender, args).onTabComplete(sender, alias, Arrays.copyOfRange(args, 1, args.length))
                : getSubcommands(sender);
    }

    private SubCommand getSubcommand(CommandSender sender, String[] args) {
        SubCommand cmd = subcommands.get(args.length > 0 ? args[0] : "help");
        return cmd != null && cmd.canUse(sender) ? cmd : subcommands.get("help");
    }

    public List<String> getSubcommands(CommandSender sender) {
        return subcommands.values()
                .stream()
                .filter(cmd -> cmd.canUse(sender))
                .map(SubCommand::getName)
                .toList();
    }
}
