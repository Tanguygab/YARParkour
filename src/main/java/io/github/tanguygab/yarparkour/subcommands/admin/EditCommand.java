package io.github.tanguygab.yarparkour.subcommands.admin;

import io.github.tanguygab.yarparkour.YARParkour;
import io.github.tanguygab.yarparkour.entities.YARPParkour;
import io.github.tanguygab.yarparkour.subcommands.SubCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EditCommand extends SubCommand {

    public EditCommand(YARParkour plugin) {
        super(plugin, "edit", true);
    }

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {
        YARPParkour parkour = getParkour(sender, args);
        if (parkour == null) return;

        String arg = args.length > 1 ? args[1] : "";
        switch (arg.toLowerCase()) {
            case "toggle" -> sendMessage(sender, getMessage("commands.edit.toggled." + (parkour.toggle() ? "enabled" : "disabled")));
            case "checkpoint" -> {
                if (!(sender instanceof Player player)) {
                    sendMessage(sender, getMessage("commands.invalid.console"));
                    return;
                }

                Location loc = player.getLocation();
                String action = args.length > 2 ? args[2] : "";
                String checkpoint = args.length > 3 ? args[3] : "";
                int index;
                try {
                    index = Integer.parseInt(checkpoint);
                } catch (NumberFormatException e) {
                    index = -1;
                }

                switch (action.toLowerCase()) {
                    case "add" -> {
                        parkour.addCheckpoint(loc);
                        sendMessage(sender, getMessage("commands.edit.checkpoint.added"));
                    }
                    case "set" -> {
                        switch (checkpoint) {
                            case "start" -> parkour.setStart(loc);
                            case "end" -> parkour.setEnd(loc);
                            default -> {
                                if (index < 0 || index >= parkour.getCheckpoints().size()) {
                                    sendMessage(sender, getMessage("commands.edit.checkpoint.invalid"));
                                    return;
                                }
                                parkour.setCheckpoint(index, loc);
                            }
                        }
                        sendMessage(sender, getMessage("commands.edit.checkpoint.set"));
                    }
                    case "insert" -> {
                        if (index < 0 || index >= parkour.getCheckpoints().size()) {
                            sendMessage(sender, getMessage("commands.edit.checkpoint.invalid"));
                            return;
                        }
                        parkour.insertCheckpoint(index, loc);
                        sendMessage(sender, getMessage("commands.edit.checkpoint.inserted"));
                    }
                    case "remove" -> {
                        switch (checkpoint) {
                            case "start" -> parkour.setStart(null);
                            case "end" -> parkour.setEnd(null);
                            default -> {
                                if (index < 0 || index >= parkour.getCheckpoints().size()) {
                                    sendMessage(sender, getMessage("commands.edit.checkpoint.invalid"));
                                    return;
                                }
                                parkour.removeCheckpoint(index);
                            }
                        }
                        sendMessage(sender, getMessage("commands.edit.checkpoint.removed"));
                    }
                    default -> sendMessage(sender, String
                            .join("\n", plugin.getMessages("commands.edit.checkpoint.usage"))
                            .replace("{parkour}", parkour.getName())
                    );
                }
            }
            default -> sendMessage(sender, getMessage("commands.edit.usage").replace("{command}", command));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String[] args) {
        return switch (args.length) {
            case 0, 1 -> plugin.getParkourManager().getParkourNames();
            case 2 -> Arrays.asList("toggle", "checkpoint");
            case 3 -> args[1].equalsIgnoreCase("checkpoint") ? Arrays.asList("add", "set", "insert", "remove") : List.of();
            case 4 -> {
                YARPParkour parkour = plugin.getParkourManager().getParkour(args[0]);
                if (parkour == null) yield List.of();
                yield switch (args[2].toLowerCase()) {
                    case "set", "remove" -> {
                        List<String> list = IntStream
                                .range(0, parkour.getCheckpoints().size())
                                .mapToObj(Integer::toString)
                                .collect(Collectors.toCollection(ArrayList::new));
                        list.add("start");
                        list.add("end");
                        yield list;
                    }
                    case "insert" -> IntStream
                            .range(0, parkour.getCheckpoints().size())
                            .mapToObj(Integer::toString)
                            .toList();
                    default -> List.of();
                };
            }
            default -> List.of();
        };
    }
}
