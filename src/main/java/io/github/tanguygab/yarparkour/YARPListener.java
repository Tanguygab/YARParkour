package io.github.tanguygab.yarparkour;

import io.github.tanguygab.yarparkour.entities.YARPParkour;
import io.github.tanguygab.yarparkour.entities.YARPPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;

public class Listener implements org.bukkit.event.Listener {

    private final YARParkour plugin;

    public Listener(YARParkour plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        // TODO: load player data
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        // TODO: save player data
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        YARPParkour parkour = plugin.getParkourFromCheckpoint(e.getBlock().getLocation());
        if (parkour != null) e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.PHYSICAL || e.getClickedBlock() == null) return;
        if (!e.getClickedBlock().getType().toString().endsWith("_PRESSURE_PLATE")) return;
        YARPParkour parkour = plugin.getParkourFromCheckpoint(e.getClickedBlock().getLocation());
        if (parkour == null) return;
        e.setCancelled(true);

        YARPPlayer player = plugin.getPlayer(e.getPlayer().getUniqueId());
        if (player.getCurrentParkour() != parkour) return;


        // TODO: check if parkour start/checkpoint/end
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {

        // TODO: check if chunk contains checkpoints & re set pressure plates & display entities
    }

}
