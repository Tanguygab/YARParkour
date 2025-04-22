package io.github.tanguygab.yarparkour.subcommands.player;

import io.github.tanguygab.yarparkour.YARParkour;
import io.github.tanguygab.yarparkour.entities.YARPPlayer;
import io.github.tanguygab.yarparkour.subcommands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class StopCommand extends SubCommand {

    public StopCommand(YARParkour plugin) {
        super(plugin, "stop");
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {
        YARPPlayer player = getPlayer(sender, args, 0);
        if (player == null) return;

        if (player.getCurrentParkour() == null) {
            sendMessage(player.getPlayer(), getMessage("parkour.no-parkour"));
            return;
        }

        player.setCurrentParkour(null);
        sendMessage(player.getPlayer(), getMessage("parkour.stopped"));
        if (sender != player.getPlayer()) sendMessage(sender, getMessage("commands.stop.stopped-for-player"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String[] args) {
        return switch (args.length) {
            case 0, 1 -> getPlayersComplete(sender);
            default -> List.of();
        };
    }
}
