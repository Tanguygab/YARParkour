package io.github.tanguygab.yarparkour.config;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class YARPItem {

    private final int slot;
    private final ItemStack item;
    private final List<String> commands;

    public YARPItem(int slot, ItemStack item, List<String> commands) {
        this.slot = slot;
        this.item = item;
        this.commands = commands;
    }

    public void giveItem(Player player) {
        player.getInventory().setItem(slot, item);
    }

    public void runCommands(Player player) {
        commands.forEach(command -> {
            command = command.replace("%player%", player.getName());
            if (command.toLowerCase().startsWith("console:"))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring(8));
            else player.performCommand(command);
        });
    }

}
