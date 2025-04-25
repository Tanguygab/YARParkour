package io.github.tanguygab.yarparkour;

import io.github.tanguygab.yarparkour.config.YARPItem;
import io.github.tanguygab.yarparkour.entities.YARPParkour;
import io.github.tanguygab.yarparkour.entities.YARPPlayer;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YARPListener implements Listener {

    private final YARParkour plugin;

    public YARPListener(YARParkour plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.getPlayerManager().loadPlayer(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.getPlayerManager().unloadPlayer(e.getPlayer().getUniqueId(), true);
        locations.remove(e.getPlayer());
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player player) {
            onItem(player, e.getCursor(), e);
            onItem(player, e.getCurrentItem(), e);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        onItem(e.getPlayer(), e.getItem(), e);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        onItem(e.getPlayer(), e.getItemDrop().getItemStack(), e);
    }

    public void onItem(Player player, ItemStack itemStack, Cancellable e) {
        YARPItem item = plugin.getConfiguration().getItem(itemStack);
        if (item == null) return;

        e.setCancelled(true);
        item.runCommands(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Location location = e.getBlock().getLocation().add(0, 1, 0);
        cancelBlockEvent(location, e);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        Location location = e.getBlock().getLocation();
        cancelBlockEvent(location, e);                               // block = checkpoint
        cancelBlockEvent(location.subtract(0, 1, 0), e);    // block = above checkpoint
        cancelBlockEvent(location.add(0, 2, 0), e);         // block = below checkpoint
    }

    public void cancelBlockEvent(Location location, Cancellable e) {
        YARPParkour parkour = plugin.getParkourManager().getParkourFromCheckpoint(location);
        if (parkour != null) e.setCancelled(true);
    }

    public final List<Player> moved = new ArrayList<>();
    private final Map<Player, Location> locations = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Location location = e.getTo();
        if (location == null) return;

        Player player = e.getPlayer();
        Location old = locations.get(e.getPlayer());
        if (old != null
                && old.getBlockX() == location.getBlockX()
                && old.getBlockY() == location.getBlockY()
                && old.getBlockZ() == location.getBlockZ()
        ) return;
        locations.put(player, location);

        YARPParkour parkour = plugin.getParkourManager().getParkourFromCheckpoint(location);
        if (parkour == null) return;

        YARPPlayer pPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());

        if (pPlayer.getCurrentParkour() != parkour) {
            if (pPlayer.getCurrentParkour() != null) {
                plugin.sendMessage(player, plugin.getMessage("parkour.already-in-parkour"));
                return;
            }

            if (plugin.getParkourManager().locationMatch(location, parkour.getStart())) {
                if (moved.contains(player)) return;
                moved.add(player);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> moved.remove(player), 40);

                if (!parkour.isEnabled()) {
                    plugin.sendMessage(player, plugin.getMessage("parkour.disabled"));
                    return;
                }
                if (parkour.notSetUp()) {
                    plugin.sendMessage(player, plugin.getMessage("parkour.not-set-up"));
                    return;
                }
                pPlayer.setCurrentParkour(parkour);
                plugin.sendMessage(player, plugin.getMessage("parkour.started"));
                return;
            }

            plugin.sendMessage(player, plugin.getMessage("parkour.not-started"));
            return;
        }

        if (plugin.getParkourManager().locationMatch(location, parkour.getStart())) {
            if (moved.contains(player)) return;
            moved.add(player);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> moved.remove(player), 40);
            plugin.sendMessage(player, plugin.getMessage("parkour.restarted"));
            pPlayer.setCurrentParkour(parkour);
            return;
        }

        if (plugin.getParkourManager().locationMatch(location, parkour.getEnd())) {
            if (pPlayer.nextCheckpoint() < parkour.getCheckpoints().size()) {
                plugin.sendMessage(player, plugin.getMessage("parkour.checkpoint.missed"));
                pPlayer.setCurrentParkour(null);
                return;
            }
            plugin.sendMessage(player, plugin
                    .getMessage("parkour.finished")
                    .replace("{time}", pPlayer.getCurrentParkourDuration())
            );
            pPlayer.setBestTime(parkour);
            pPlayer.setCurrentParkour(null);
            return;
        }

        int checkpoint = plugin.getParkourManager().getCheckpoint(parkour, location);
        if (checkpoint <= pPlayer.getCurrentParkourCheckpoint()) return;

        if (pPlayer.nextCheckpoint() != checkpoint) {
            plugin.sendMessage(player, plugin.getMessage("parkour.checkpoint.missed"));
            pPlayer.setCurrentParkour(null);
            return;
        }

        plugin.sendMessage(player, plugin
                .getMessage("parkour.checkpoint.reached")
                .replace("{time}", pPlayer.getCurrentParkourDuration())
                .replace("{checkpoint}", String.valueOf(checkpoint + 1))
        );
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if (e.isNewChunk()) return;
        Chunk chunk = e.getChunk();

        plugin.getParkourManager().getParkours().forEach(parkour -> {
            if (parkour.getStart() != null && parkour.getStart().getChunk() == chunk) parkour.setStart(parkour.getStart());
            if (parkour.getEnd() != null && parkour.getEnd().getChunk() == chunk) parkour.setStart(parkour.getEnd());
            for (int i = 0; i < parkour.getCheckpoints().size(); i++) {
                Location checkpoint = parkour.getCheckpoints().get(i);
                if (checkpoint.getChunk() == chunk) parkour.setCheckpoint(i, checkpoint);
            }
        });
    }

}
