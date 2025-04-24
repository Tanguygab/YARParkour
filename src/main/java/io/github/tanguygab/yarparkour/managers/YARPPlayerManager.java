package io.github.tanguygab.yarparkour.managers;

import io.github.tanguygab.yarparkour.YARParkour;
import io.github.tanguygab.yarparkour.entities.YARPParkour;
import io.github.tanguygab.yarparkour.entities.YARPPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

        players.put(player.getUniqueId(), new YARPPlayer(player, bestTimes));
    }

    @Override
    public void unload() {
        players.keySet().forEach(uuid -> unloadPlayer(uuid, false));
        players.clear();
        save();
    }

    public void unloadPlayer(UUID uuid, boolean remove) {
        Map<String, Long> bestTimes = new HashMap<>();
        players.get(uuid).getBestTimes().forEach((parkour, time) -> bestTimes.put(parkour.getName(), time));
        data.set(uuid + ".best-times", bestTimes);
        if (remove) players.remove(uuid);
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
}
