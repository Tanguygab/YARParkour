package io.github.tanguygab.yarparkour.subcommands.player;

import io.github.tanguygab.yarparkour.YARParkour;
import io.github.tanguygab.yarparkour.entities.YARPPlayer;
import io.github.tanguygab.yarparkour.subcommands.SubCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ResetCommand extends SubCommand {

    public ResetCommand(YARParkour plugin) {
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

        Location location = player.getCurrentParkour().getStart();
        player.getPlayer().teleport(location);
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
