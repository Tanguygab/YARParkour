package io.github.tanguygab.yarparkour.subcommands;

import io.github.tanguygab.yarparkour.YARParkour;
import io.github.tanguygab.yarparkour.entities.YARPParkour;
import io.github.tanguygab.yarparkour.entities.YARPPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

public abstract class SubCommand {

    protected final YARParkour plugin;
    private final String name;
    private final boolean permission;

    public SubCommand(YARParkour plugin, String name, boolean permission) {
        this.plugin = plugin;
        this.name = name;
        this.permission = permission;
    }

    public SubCommand(YARParkour plugin, String name) {
        this(plugin, name, false);
    }

    public String getName() {
        return name;
    }

    public boolean canUse(CommandSender sender) {
        return !permission || sender.hasPermission("yarparkour." + name);
    }

    private boolean canUseOther(CommandSender sender) {
        return sender.hasPermission("yarparkour." + name + ".other");
    }

    protected List<String> getPlayersComplete(CommandSender sender) {
        return canUseOther(sender) ? null : List.of();
    }

    @Nullable
    protected YARPPlayer getPlayer(CommandSender sender, String[] args, int index) {
        String name = args.length > index ? args[index] : "";

        if (!name.isEmpty() && !canUseOther(sender)) {
            sendMessage(sender, getMessage("commands.invalid.permission"));
            return null;
        }

        YARPPlayer player = name.isEmpty() && sender instanceof Player p
                ? plugin.getPlayerManager().getPlayer(p.getUniqueId())
                : plugin.getPlayerManager().getPlayer(name);

        if (player == null) sendMessage(sender, getMessage("commands.invalid.player").replace("{player}", name));
        return player;
    }

    @Nullable
    protected YARPParkour getParkour(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, getMessage("commands.invalid.no-name"));
            return null;
        }

        YARPParkour parkour = plugin.getParkourManager().getParkour(args[0]);
        if (parkour == null) sendMessage(sender, getMessage("commands.invalid.parkour").replace("{parkour}", args[0]));
        return parkour;
    }

    protected String getMessage(String path) {
        return plugin.getMessage(path);
    }

    protected void sendMessage(CommandSender sender, String message) {
        plugin.sendMessage(sender, message);
    }

    public abstract void onCommand(CommandSender sender, String command, String[] args);
    public abstract List<String> onTabComplete(CommandSender sender, String command, String[] args);
}
