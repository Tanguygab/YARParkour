package io.github.tanguygab.yarparkour.entities;

import io.github.tanguygab.yarparkour.YARParkour;
import io.github.tanguygab.yarparkour.config.YARPConfig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class YARPPlayer {

    private final Player player;
    private final Map<YARPParkour, Long> bestTimes;
    private YARPParkour currentParkour;
    private int currentParkourCheckpoint;
    private LocalDateTime currentParkourStart;
    private ItemStack[] inventory;


    public YARPPlayer(Player player, Map<YARPParkour, Long> bestTimes) {
        this.player = player;
        this.bestTimes = bestTimes;
    }

    public YARPPlayer(Player player, Map<YARPParkour, Long> bestTimes, YARPParkour parkour, int checkpoint, LocalDateTime start) {
        this(player, bestTimes);
        this.currentParkour = parkour;
        this.currentParkourCheckpoint = checkpoint;
        this.currentParkourStart = start;
    }

    public Player getPlayer() {
        return player;
    }

    public YARPParkour getCurrentParkour() {
        return currentParkour;
    }

    public void setCurrentParkour(YARPParkour parkour) {
        if (currentParkour != null && inventory != null) restoreInventory();
        currentParkour = parkour;
        currentParkourCheckpoint = -1;
        currentParkourStart = null;
        YARParkour plugin = (YARParkour) YARParkour.getProvidingPlugin(YARParkour.class);
        plugin.getPlayerManager().removeTeam(player);
        if (parkour != null) {
            plugin.getPlayerManager().addTeam(player);
            YARPConfig config = plugin.getConfiguration();
            giveItems(config);
            currentParkourStart = LocalDateTime.now();
            if (config.teleportOnStart()) config.teleport(player, parkour.getStart());
        }
    }

    public void restoreInventory() {
        player.getInventory().setContents(inventory);
    }

    public void giveItems(YARPConfig config) {
        inventory = player.getInventory().getContents().clone();
        player.getInventory().clear();
        config.giveItems(player);
    }

    public int getCurrentParkourCheckpoint() {
        return currentParkourCheckpoint;
    }

    public int nextCheckpoint() {
        return ++currentParkourCheckpoint;
    }

    public LocalDateTime getCurrentParkourStart() {
        return currentParkourStart;
    }

    public long getCurrentParkourTime() {
        return ChronoUnit.MILLIS.between(currentParkourStart, LocalDateTime.now());
    }

    public String getCurrentParkourDuration() {
        long ms = getCurrentParkourTime();
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        ms %= 1000;
        seconds %= 60;
        minutes %= 60;

        String duration = hours > 0 ? hours + "h" : "";
        if (minutes > 0) duration += (duration.isEmpty() ? "" : " ") + minutes + "m";
        if (seconds > 0) duration += (duration.isEmpty() ? "" : " ") + seconds + "s";
        if (ms > 0) duration += (duration.isEmpty() ? "" : " ") + ms + "ms";
        return duration;
    }

    public Map<YARPParkour, Long> getBestTimes() {
        return bestTimes;
    }

    public double getBestTime(YARPParkour parkour) {
        return bestTimes.getOrDefault(parkour, -1L);
    }

    public void setBestTime(YARPParkour parkour) {
        long time = getCurrentParkourTime();
        if (time == -1L || time < getBestTime(parkour)) bestTimes.put(parkour, time);
    }
}
