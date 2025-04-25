package io.github.tanguygab.yarparkour.config;

import io.github.tanguygab.yarparkour.YARParkour;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YARPConfig {

    private final NamespacedKey ITEM_KEY;

    private final Map<YARPCheckpointType, YARPCheckpointConfig> checkpoints;
    private final double distance;
    private final boolean teleportOnStart;
    private final boolean teleportWithDirection;
    private final boolean rememberOnLogout;

    private final Map<String, YARPItem> items = new HashMap<>();

    public YARPConfig(YARParkour plugin) {
        ITEM_KEY = new NamespacedKey(plugin, "item");
        ConfigurationSection config = plugin.getConfig();

        distance = config.getDouble("distance", 1.5);
        teleportOnStart = config.getBoolean("teleport-on-start", false);
        teleportWithDirection = config.getBoolean("teleport-with-direction", true);
        rememberOnLogout = config.getBoolean("remember-on-logout", true);

        checkpoints = Map.of(
                YARPCheckpointType.START, getCheckpointConfig(config, "start"),
                YARPCheckpointType.CHECKPOINT, getCheckpointConfig(config, "checkpoint"),
                YARPCheckpointType.END, getCheckpointConfig(config, "end")
        );

        ConfigurationSection items = config.getConfigurationSection("items");
        if (items != null) items.getKeys(false).forEach(id -> {
            String materialString = items.getString(id + ".material");
            if (materialString == null) return;
            Material material = Material.getMaterial(materialString);
            if (material == null) return;

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String name = items.getString(id + ".name", "");
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

                meta.getPersistentDataContainer().set(ITEM_KEY, PersistentDataType.STRING, id);

                item.setItemMeta(meta);
            }

            int slot = items.getInt(id + ".slot");
            List<String> commands = items.getStringList(id + ".commands");

            this.items.put(id, new YARPItem(slot, item, commands));
        });
    }

    public double getDistance() {
        return distance;
    }

    private YARPCheckpointConfig getCheckpointConfig(ConfigurationSection config, String name) {
        return new YARPCheckpointConfig(config.getConfigurationSection("checkpoints." + name));
    }

    public YARPCheckpointConfig getCheckpoint(YARPCheckpointType type) {
        return checkpoints.get(type);
    }

    public YARPItem getItem(ItemStack item) {
        if (item == null
                || item.getItemMeta() == null
                || !item.getItemMeta().getPersistentDataContainer().has(ITEM_KEY, PersistentDataType.STRING)
        ) return null;

        return items.get(item.getItemMeta().getPersistentDataContainer().get(ITEM_KEY, PersistentDataType.STRING));
    }

    public void giveItems(Player player) {
        items.values().forEach(item -> item.giveItem(player));
    }

    public boolean teleportOnStart() {
        return teleportOnStart;
    }

    public void teleport(Player player, Location location) {
        if (!teleportWithDirection) {
            location.setYaw(player.getLocation().getYaw());
            location.setPitch(player.getLocation().getPitch());
        }
        player.teleport(location);
    }

    public boolean rememberOnLogout() {
        return rememberOnLogout;
    }
}
