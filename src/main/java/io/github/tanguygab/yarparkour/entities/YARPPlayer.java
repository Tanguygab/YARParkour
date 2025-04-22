package io.github.tanguygab.yarparkour.entities;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class YARPPlayer {

    private final Player player;
    private final Map<YARPParkour, Double> bestTimes;
    private YARPParkour currentParkour;
    private int currentParkourCheckpoint;
    private LocalDateTime currentParkourStart;

    public YARPPlayer(Player player, Map<YARPParkour, Double> bestTimes) {
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
        currentParkour = parkour;
        currentParkourCheckpoint = -1;
        currentParkourStart = null;
        if (parkour != null) {
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
        return ChronoUnit.SECONDS.between(currentParkourStart, LocalDateTime.now());
    }

    public String getCurrentParkourDuration() {
        long seconds = getCurrentParkourTime();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds %= 60;
        minutes %= 60;

        String duration = hours > 0 ? hours + "h" : "";
        if (minutes > 0) duration += (duration.isEmpty() ? "" : " ") + minutes + "m";
        if (seconds > 0) duration += (duration.isEmpty() ? "" : " ") + seconds + "s";
        return duration;
    }

    public Map<YARPParkour, Double> getBestTimes() {
        return bestTimes;
    }

    public double getBestTime(YARPParkour parkour) {
        return bestTimes.getOrDefault(parkour, -1.);
    }

    public void setBestTime(YARPParkour parkour, double time) {
        bestTimes.put(parkour, time);
    }
}
