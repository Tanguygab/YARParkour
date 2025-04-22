package io.github.tanguygab.yarparkour;

import io.github.tanguygab.yarparkour.subcommands.HelpCommand;
import io.github.tanguygab.yarparkour.subcommands.ReloadCommand;
import io.github.tanguygab.yarparkour.subcommands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class YARParkour extends JavaPlugin {

    private final Map<String, SubCommand> subcommands = Map.of(
            "help", new HelpCommand(this),
            "start", null,
            "checkpoint", null,
            "stop", null,
            "reload", new ReloadCommand(this)
    );

    private YARPExpansion expansion;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Listener(this), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String arg = args.length > 0 ? args[0] : "help";
        subcommands.getOrDefault(arg, subcommands.get("help"))
                .onCommand(sender, label, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length > 0 && subcommands.containsKey(args[0]))
            return subcommands.get(args[0]).onTabComplete(sender, alias, Arrays.copyOfRange(args, 1, args.length));
        return new ArrayList<>(subcommands.keySet());
    }
}
