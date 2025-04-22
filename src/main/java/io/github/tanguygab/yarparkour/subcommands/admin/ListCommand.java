package io.github.tanguygab.yarparkour.subcommands.admin;

import io.github.tanguygab.yarparkour.YARParkour;
import io.github.tanguygab.yarparkour.entities.YARPParkour;
import io.github.tanguygab.yarparkour.subcommands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ListCommand extends SubCommand {

    private static final int MAX_PER_PAGE = 10;

    private final List<String> msg = plugin.getMessages("commands.list.message");
    private final String parkourMsg = getMessage("commands.list.parkour");

    public ListCommand(YARParkour plugin) {
        super(plugin, "list", true);
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {
        int page;
        try {
            page = args.length > 0 ? Integer.parseInt(args[0]) : 1;
        } catch (Exception e) {
            page = 1;
        }
        --page;

        List<String> msgs = new ArrayList<>();
        List<YARPParkour> parkours = plugin.getParkourManager().getParkours();
        for (int i = page * MAX_PER_PAGE; i < page * MAX_PER_PAGE + MAX_PER_PAGE; i++) {
            if (i >= parkours.size()) break;
            YARPParkour parkour = parkours.get(i);
            msgs.add(parkourMsg
                    .replace("{enabled}", parkour.isEnabled() ? "&a" : "&c")
                    .replace("{parkour}", parkour.getName())
            );
        }

        sendMessage(sender, String.join("\n", msg)
                .replace("{parkours}", String.join("\n", msgs))
                .replace("{page}", String.valueOf(page))
                .replace("{maxpage}", String.valueOf(getMaxPage()))
                .replace("{enabled}", String.valueOf(plugin.getParkourManager().getEnabledParkours().size()))
                .replace("{total}", String.valueOf(parkours.size()))
        );
    }

    private int getMaxPage() {
        return Math.ceilDiv(plugin.getParkourManager().getParkourNames().size(), MAX_PER_PAGE);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String[] args) {
        return args.length > 1 ? List.of() : IntStream.rangeClosed(1, getMaxPage()).mapToObj(String::valueOf).toList();
    }
}
