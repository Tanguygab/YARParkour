package io.github.tanguygab.yarparkour.entities;

import io.github.tanguygab.yarparkour.YARParkour;
import io.github.tanguygab.yarparkour.config.YARPCheckpointConfig;
import io.github.tanguygab.yarparkour.config.YARPCheckpointType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class YARPParkour {

    private static final NamespacedKey PARKOUR_KEY = new NamespacedKey(YARParkour.getProvidingPlugin(YARParkour.class), "parkour");

    private final String name;
    private boolean enabled;
    private Location start;
    private Location end;
    private final List<Location> checkpoints = new ArrayList<>();

    public YARPParkour(String name, boolean enabled, Location start, Location end, List<Location> checkpoints) {
        this.name = name;
        this.enabled = enabled;
        setStart(start);
        setEnd(end);
        checkpoints.forEach(this::addCheckpoint);
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean notSetUp() {
        return start == null || end == null;
    }

    public boolean toggle() {
        return enabled = !enabled;
    }

    public Location getStart() {
        return start;
    }
    private void setCheckpoint(Location location, YARPCheckpointType type, String name) {
        if (location == null || !location.isWorldLoaded() || location.getWorld() == null || !location.getChunk().isLoaded()) return;
        YARPCheckpointConfig config = YARParkour.getPlugin(YARParkour.class).getConfiguration().getCheckpoint(type);
        location.subtract(0, 1, 0).getBlock().setType(config.getBlock());


        TextDisplay display = location.getWorld().spawn(location.add(0, 3, 0), TextDisplay.class);
        location.subtract(0, 2, 0);
        display.setPersistent(false);
        display.setText(ChatColor.translateAlternateColorCodes('&', config
                .getText()
                .replace("{parkour}", this.name)
                .replace("{checkpoint}", name)
        ));
        display.setBillboard(Display.Billboard.CENTER);

        // TODO: Listen to outgoing entity packets & replace {best} with the player's best time for the chosen parkour
        display.getPersistentDataContainer().set(PARKOUR_KEY, PersistentDataType.STRING, this.name);
    }

    private void clearCheckpoint(Location location) {
        location.subtract(0, 1, 0).getBlock().setType(Material.AIR);

        assert location.getWorld() != null;
        location.getWorld().getNearbyEntities(
                location.add(0, 3, 0), 2, 2, 2,
                entity -> entity.getPersistentDataContainer().has(PARKOUR_KEY)
        ).forEach(Entity::remove);
    }

    public void setStart(Location start) {
        if (this.start != null) clearCheckpoint(this.start);
        setCheckpoint(start, YARPCheckpointType.START, "Start");
        this.start = start;
    }

    public Location getEnd() {
        return end;
    }

    public void setEnd(Location end) {
        if (this.end != null) clearCheckpoint(this.end);
        setCheckpoint(end, YARPCheckpointType.END, "End");
        this.end = end;
    }

    public List<Location> getCheckpoints() {
        return checkpoints;
    }

    public void addCheckpoint(Location location) {
        checkpoints.add(location);
        setCheckpoint(location, YARPCheckpointType.CHECKPOINT, String.valueOf(checkpoints.size()));
    }

    public void setCheckpoint(int index, Location location) {
        clearCheckpoint(checkpoints.get(index));
        checkpoints.set(index, location);
        setCheckpoint(location, YARPCheckpointType.CHECKPOINT, String.valueOf(index + 1));
    }

    public void insertCheckpoint(int index, Location location) {
        checkpoints.add(index, location);
        updateCheckpointsFrom(index);
    }

    public void removeCheckpoint(int index) {
        Location location = checkpoints.get(index);
        clearCheckpoint(location);
        checkpoints.remove(location);
        updateCheckpointsFrom(index);
    }

    private void updateCheckpointsFrom(int index) {
        for (int i = index; i < checkpoints.size(); i++) {
            clearCheckpoint(checkpoints.get(index));
            setCheckpoint(i, checkpoints.get(index));
        }
    }
}
