package io.github.tanguygab.yarparkour.subcommands.player;

import io.github.tanguygab.yarparkour.YARParkour;
import io.github.tanguygab.yarparkour.entities.YARPParkour;
import io.github.tanguygab.yarparkour.entities.YARPPlayer;
import io.github.tanguygab.yarparkour.subcommands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class StartCommand extends SubCommand {

    public StartCommand(YARParkour plugin) {
        super(plugin, "start");
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {
        String parkourName = args.length > 0 ? args[0] : "";
        YARPParkour parkour = plugin.getParkourManager().getParkour(parkourName);
        if (parkour == null) {
            sendMessage(sender, getMessage("commands.invalid.parkour").replace("{parkour}", parkourName));
            return;
        }

        if (parkour.notSetUp()) {
            sendMessage(sender, getMessage("parkour.not-set-up"));
            return;
        }

        if (!parkour.isEnabled()) {
            sendMessage(sender, getMessage("parkour.disabled"));
            return;
        }

        YARPPlayer player = getPlayer(sender, args, 1);
        if (player == null) return;

        player.setCurrentParkour(parkour);
        sendMessage(player.getPlayer(), getMessage("parkour.started"));
        if (sender != player.getPlayer()) sendMessage(sender, getMessage("commands.stop.started-for-player"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String[] args) {
        return switch (args.length) {
            case 0, 1 -> plugin.getParkourManager().getEnabledParkours();
            case 2 -> getPlayersComplete(sender);
            default -> List.of();
        };
    }
}
