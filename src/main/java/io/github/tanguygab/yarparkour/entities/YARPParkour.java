package io.github.tanguygab.yarparkour;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

public class YARPParkour {

    private final String name;
    private boolean enabled;
    private Location start;
    private Location end;
    private final List<Location> checkpoints;

    public YARPParkour(String name, boolean enabled, List<Location> checkpoints) {
        this.name = name;
        this.enabled = enabled;
        this.checkpoints = checkpoints;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Location getStart() {
        return start;
    }

    private void clearCheckpoint(Location location) {
        location.getBlock().setType(Material.AIR);
        // TODO: remove display entity
    }

    public void setStart(Location start) {
        if (this.start != null) clearCheckpoint(this.start);
        this.start = start;
    }

    public Location getEnd() {
        return end;
    }

    public void setEnd(Location end) {
        if (this.end != null) clearCheckpoint(this.end);
        this.end = end;
    }

    public List<Location> getCheckpoints() {
        return checkpoints;
    }

    public void addCheckpoint(Location location, Material plate) {
        location.getBlock().setType(plate);
        checkpoints.add(location);
    }

    public void removeCheckpoint(Location location) {
        clearCheckpoint(location);
        checkpoints.remove(location);
    }
}
