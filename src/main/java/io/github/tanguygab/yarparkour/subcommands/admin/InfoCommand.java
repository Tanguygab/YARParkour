package io.github.tanguygab.yarparkour.subcommands.admin;

import io.github.tanguygab.yarparkour.YARParkour;
import io.github.tanguygab.yarparkour.entities.YARPParkour;
import io.github.tanguygab.yarparkour.subcommands.SubCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class InfoCommand extends SubCommand {

    private final List<String> msg = plugin.getMessages("commands.info.message");
    private final String checkpointMsg = getMessage("commands.info.checkpoint");
    private final String unset = getMessage("commands.info.unset");
    private final String location = getMessage("commands.info.location");

    public InfoCommand(YARParkour plugin) {
        super(plugin, "delete", true);
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {
        YARPParkour parkour = getParkour(sender, args);
        if (parkour == null) return;

        List<String> checkpoints = new ArrayList<>();
        for (Location checkpoint : parkour.getCheckpoints()) {
            checkpoints.add(replaceLoc(
                    checkpointMsg.replace("{checkpoint}", String.valueOf(parkour.getCheckpoints().indexOf(checkpoint))),
                    "",
                    checkpoint
            ));
        }
        String msg = String.join("\n", this.msg)
                .replace("{checkpoints}", String.join("\n", checkpoints))
                .replace("{status}", getMessage("commands.info.status." + (parkour.isEnabled() ? "enabled" : "disabled")))
                .replace("{parkour}", parkour.getName());
        msg = replaceLoc(msg, "start-", parkour.getStart());
        msg = replaceLoc(msg, "end-", parkour.getEnd());
        sendMessage(sender, msg);
    }

    private String replaceLoc(String string, String key, Location loc) {
        if (loc == null) return string.replace("{" + key + "location}", unset);

        assert loc.getWorld() != null;
        return string.replace("{" + key + "location}", location)
                .replace("{world}", loc.getWorld().getName())
                .replace("{x}", String.valueOf(loc.getBlockX()))
                .replace("{y}", String.valueOf(loc.getBlockY()))
                .replace("{z}", String.valueOf(loc.getBlockZ()));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String[] args) {
        return args.length < 2 ? plugin.getParkourManager().getParkourNames() : List.of();
    }
}
