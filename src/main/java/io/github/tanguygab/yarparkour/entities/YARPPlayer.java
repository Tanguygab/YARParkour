package io.github.tanguygab.yarparkour.entities;

import io.github.tanguygab.yarparkour.YARParkour;
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

    public Player getPlayer() {
        return player;
    }

    public YARPParkour getCurrentParkour() {
        return currentParkour;
    }

    public void setCurrentParkour(YARPParkour parkour) {
        if (currentParkour != null && inventory != null) {
            player.getInventory().clear();
            player.getInventory().setContents(inventory);
        }
        currentParkour = parkour;
        currentParkourCheckpoint = -1;
        currentParkourStart = null;
        if (parkour != null) {
            inventory = player.getInventory().getContents().clone();
            player.getInventory().clear();
            ((YARParkour)YARParkour.getProvidingPlugin(YARParkour.class)).getConfiguration().giveItems(player);
            currentParkourStart = LocalDateTime.now();
            player.teleport(parkour.getStart());
        }
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
        long seconds = ms / 100;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        ms %= 100;
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
