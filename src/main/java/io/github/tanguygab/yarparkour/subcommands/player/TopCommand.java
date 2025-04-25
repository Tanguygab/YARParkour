package io.github.tanguygab.yarparkour.subcommands.player;

import io.github.tanguygab.yarparkour.YARParkour;
import io.github.tanguygab.yarparkour.entities.YARPParkour;
import io.github.tanguygab.yarparkour.entities.YARPPlayer;
import io.github.tanguygab.yarparkour.subcommands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TopCommand extends SubCommand {

    private final List<String> msg = plugin.getMessages("commands.top.message");
    private final String playerMsg = getMessage("commands.top.player");

    public TopCommand(YARParkour plugin) {
        super(plugin, "top");
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {
        String parkourName = args.length > 0 ? args[0] : "";
        YARPParkour parkour = null;

        if (parkourName.isEmpty() && sender instanceof Player player)
            parkour = plugin.getPlayerManager().getPlayer(player.getUniqueId()).getCurrentParkour();
        if (parkour == null) parkour = plugin.getParkourManager().getParkour(parkourName);
        if (parkour == null) {
            sendMessage(sender, getMessage("commands.invalid.parkour").replace("{parkour}", parkourName));
            return;
        }

        List<String> msgs = new ArrayList<>();
        List<YARPPlayer> players = plugin.getPlayerManager().getPlayers(parkour);
        for (YARPPlayer player : players) {
            msgs.add(playerMsg
                    .replace("{position}", String.valueOf(players.indexOf(player) + 1))
                    .replace("{player}", player.getPlayer().getName())
                    .replace("{time}", String.valueOf(player.getCurrentParkourTime()))
                    .replace("{duration}", player.getCurrentParkourDuration())
                    .replace("{checkpoint}", String.valueOf(player.getCurrentParkourCheckpoint() + 1))
            );
        }

        sendMessage(sender, String.join("\n", msg)
                .replace("{players}", String.join("\n", msgs))
        );
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String[] args) {
        return switch (args.length) {
            case 0, 1 -> getPlayersComplete(sender);
            default -> List.of();
        };
    }
}
