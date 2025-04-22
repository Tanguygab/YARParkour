package io.github.tanguygab.yarparkour;

import io.github.tanguygab.yarparkour.entities.YARPParkour;
import io.github.tanguygab.yarparkour.entities.YARPPlayer;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class YARPExpansion extends PlaceholderExpansion {

    private final YARParkour plugin;

    public YARPExpansion(YARParkour plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return plugin.getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return List.of(
                "%yarparkour_is_in_parkour%",
                "%yarparkour_current_parkour%",
                "%yarparkour_current_start%",
                "%yarparkour_current_seconds%",
                "%yarparkour_current_time%",
                "%yarparkour_current_checkpoint%",
                "%yarparkour_best_time_<parkour>%"
        );
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        YARPPlayer player = plugin.getPlayerManager().getPlayer(offlinePlayer.getUniqueId());
        if (player == null) return null;

        YARPParkour parkour = player.getCurrentParkour();
        return switch (params) {
            case "is_in_parkour" -> parkour == null
                    ? PlaceholderAPIPlugin.booleanFalse()
                    : PlaceholderAPIPlugin.booleanTrue();
            case "current_parkour" -> parkour == null ? "" : parkour.getName();
            case "current_start" -> player.getCurrentParkourStart() == null
                    ? ""
                    : PlaceholderAPIPlugin.getDateFormat().format(player.getCurrentParkourStart());
            case "current_seconds" -> player.getCurrentParkourStart() == null
                    ? "0"
                    : String.valueOf(player.getCurrentParkourTime());
            case "current_time" -> player.getCurrentParkourStart() == null
                    ? "0"
                    : String.valueOf(player.getCurrentParkourDuration());
            case "current_checkpoint" -> String.valueOf(player.getCurrentParkourCheckpoint() + 1);
            default -> {
                if (params.startsWith("best_time_")) {
                    String parkourName = params.substring(10);
                    parkour = plugin.getParkourManager().getParkour(parkourName);
                    yield parkour == null
                            ? "Invalid Parkour"
                            : String.valueOf(player.getBestTime(parkour));
                }
                yield null;
            }
        };
    }



}
