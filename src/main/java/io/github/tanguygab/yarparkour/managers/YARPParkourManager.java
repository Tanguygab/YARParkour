package io.github.tanguygab.yarparkour.managers;

import io.github.tanguygab.yarparkour.YARParkour;
import io.github.tanguygab.yarparkour.entities.YARPParkour;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YARPParkourManager extends YARPManager {

    private final Map<String, YARPParkour> parkours = new HashMap<>();
    private final double distance = plugin.getConfiguration().getDistance();

    public YARPParkourManager(YARParkour plugin) {
        super(plugin, "parkours.yml");
    }

    @Override
    public void load() {
        data.getKeys(false).forEach(parkour -> {
            ConfigurationSection config = data.getConfigurationSection(parkour);
            assert config != null;

            boolean enabled = config.getBoolean("enabled", false);
            @SuppressWarnings("unchecked")
            List<Location> checkpoints = (List<Location>) config.getList("checkpoints", List.of());

            Location start = config.getLocation("start");
            Location end = config.getLocation("end");
            parkours.put(parkour, new YARPParkour(parkour, enabled, start, end, checkpoints));
        });
    }

    @Override
    public void unload() {
        parkours.forEach((name, parkour) -> {
            data.set(name + ".enabled", parkour.isEnabled());
            data.set(name + ".start", parkour.getStart());
            data.set(name + ".end", parkour.getEnd());
            data.set(name + ".checkpoints", parkour.getCheckpoints());
        });
        save();
    }

    public List<YARPParkour> getParkours() {
        return new ArrayList<>(parkours.values());
    }

    public List<String> getParkourNames() {
        return new ArrayList<>(parkours.keySet());
    }

    public List<String> getEnabledParkours() {
        return parkours.values()
                .stream()
                .filter(YARPParkour::isEnabled)
                .map(YARPParkour::getName)
                .toList();
    }

    public void createParkour(String name) {
        parkours.put(name, new YARPParkour(name, false, null, null, new ArrayList<>()));
    }

    public void deleteParkour(String name) {
        parkours.remove(name);
    }

    public YARPParkour getParkour(String name) {
        return parkours.get(name);
    }

    public boolean locationMatch(Location loc1, Location loc2) {
        return loc2 != null && loc1.getWorld() == loc2.getWorld() && loc2.distance(loc1) <= distance;
    }

    public int getCheckpoint(YARPParkour parkour, Location location) {
        for (Location checkpoint : parkour.getCheckpoints())
            if (locationMatch(location, checkpoint))
                return parkour.getCheckpoints().indexOf(checkpoint);
        return -1;
    }

    public YARPParkour getParkourFromCheckpoint(Location location) {
        for (YARPParkour parkour : parkours.values()) {
            if (locationMatch(location, parkour.getStart())) return parkour;
            if (locationMatch(location, parkour.getEnd())) return parkour;
            if (getCheckpoint(parkour, location) > -1) return parkour;
        }
        return null;
    }
}
