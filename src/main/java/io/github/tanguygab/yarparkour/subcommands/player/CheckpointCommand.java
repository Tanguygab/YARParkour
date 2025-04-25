package io.github.tanguygab.yarparkour.subcommands.player;

import io.github.tanguygab.yarparkour.YARParkour;
import io.github.tanguygab.yarparkour.entities.YARPPlayer;
import io.github.tanguygab.yarparkour.subcommands.SubCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CheckpointCommand extends SubCommand {

    public CheckpointCommand(YARParkour plugin) {
        super(plugin, "checkpoint");
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {
        YARPPlayer player = getPlayer(sender, args, 0);
        if (player == null) return;

        if (player.getCurrentParkour() == null) {
            sendMessage(player.getPlayer(), getMessage("parkour.no-parkour"));
            return;
        }

        int checkpoint = player.getCurrentParkourCheckpoint();
        Location location = checkpoint < 0
                ? player.getCurrentParkour().getStart()
                : player.getCurrentParkour().getCheckpoints().get(checkpoint);

        plugin.getConfiguration().teleport(player.getPlayer(), location);

        sendMessage(player.getPlayer(), getMessage("commands.checkpoint.teleported"));
        if (sender != player.getPlayer()) sendMessage(sender, getMessage("commands.checkpoint.teleported"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String[] args) {
        return switch (args.length) {
            case 0, 1 -> getPlayersComplete(sender);
            default -> List.of();
        };
    }
}
