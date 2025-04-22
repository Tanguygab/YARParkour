package io.github.tanguygab.yarparkour;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class YARParkourExpansion extends PlaceholderExpansion {

    private final YARParkour plugin;

    public YARParkourExpansion(YARParkour plugin) {
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
                "%yarparkour_parkour%",
                "%yarparkour_time%",
                "%yarparkour_checkpoint%",
                "%yarparkour_best_time_<parkour>%",
                "%yarparkour_%",
                "%yarparkour_%"
        );
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        YARPPlayer player = plugin.getPlayer(offlinePlayer.getUniqueId());
        if ()


        return null;
    }
}
