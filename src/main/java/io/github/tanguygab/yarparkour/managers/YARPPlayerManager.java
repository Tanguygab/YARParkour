package io.github.tanguygab.yarparkour.managers;

import io.github.tanguygab.yarparkour.YARParkour;
import io.github.tanguygab.yarparkour.entities.YARPParkour;
import io.github.tanguygab.yarparkour.entities.YARPPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.*;

public class YARPPlayerManager extends YARPManager {

    private final Map<UUID, YARPPlayer> players = new HashMap<>();

    public YARPPlayerManager(YARParkour plugin) {
        super(plugin, "players.yml");
    }

    @Override
    public void load() {
        plugin.getServer().getOnlinePlayers().forEach(this::loadPlayer);
    }

    public void loadPlayer(Player player) {
        ConfigurationSection config = data.getConfigurationSection(player.getUniqueId().toString());

        Map<YARPParkour, Long> bestTimes = new HashMap<>();
        if (config == null) {
            players.put(player.getUniqueId(), new YARPPlayer(player, bestTimes));
            return;
        }

        ConfigurationSection bestTimeSection = config.getConfigurationSection("best-times");
        if (bestTimeSection != null) {
            bestTimeSection.getValues(false).forEach((parkourName, time) -> {
                YARPParkour parkour = plugin.getParkourManager().getParkour(parkourName);
                if (parkour == null) {
                    plugin.getLogger().warning("Parkour " + parkourName + " not found, skipping best time for " + player.getName());
                    return;
                }
                bestTimes.put(parkour, (long) time);
            });
        }

        YARPPlayer pPlayer;

        if (config.contains("current")) {
            YARPParkour parkour = plugin.getParkourManager().getParkour(config.getString("current.parkour"));
            int checkpoint = config.getInt("current.checkpoint");
            LocalDateTime start = LocalDateTime.parse(config.getString("current.start", ""));
            pPlayer = new YARPPlayer(player, bestTimes, parkour, checkpoint, start);
            pPlayer.giveItems(plugin.getConfiguration());
        } else pPlayer = new YARPPlayer(player, bestTimes);

        players.put(player.getUniqueId(), pPlayer);
    }

    @Override
    public void unload() {
        players.keySet().forEach(uuid -> unloadPlayer(uuid, false));
        players.clear();
        save();
    }

    public void unloadPlayer(UUID uuid, boolean save) {
        YARPPlayer player = players.get(uuid);
        Map<String, Long> bestTimes = new HashMap<>();
        player.getBestTimes().forEach((parkour, time) -> bestTimes.put(parkour.getName(), time));
        data.set(uuid + ".best-times", bestTimes);

        if (player.getCurrentParkour() != null) {
            player.restoreInventory();
            if (plugin.getConfiguration().rememberOnLogout()) {
                data.set(uuid + ".current.parkour", player.getCurrentParkour().getName());
                data.set(uuid + ".current.checkpoint", player.getCurrentParkourCheckpoint());
                data.set(uuid + ".current.start", player.getCurrentParkourStart().toString());
            }
        }

        if (!save) return;
        players.remove(uuid);
        save();
    }

    public YARPPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public YARPPlayer getPlayer(String name) {
        for (YARPPlayer player : players.values())
            if (name.equalsIgnoreCase(player.getPlayer().getName()))
                return player;
        return null;
    }

    public List<YARPPlayer> getPlayers(YARPParkour parkour) {
        return players.values()
                .stream()
                .filter(player -> player.getCurrentParkour() == parkour)
                .sorted(Comparator
                        .comparingInt(YARPPlayer::getCurrentParkourCheckpoint)
                        .reversed()
                        .thenComparingLong(YARPPlayer::getCurrentParkourTime)
                )
                .toList();
    }
}
