package com.gmail.woodyc40.staticplayerinv;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin implements Listener {
    private static final int PLAYER_CRAFT_INV_SIZE = 5;
    private static final long INV_UPDATE_INTERVAL = 20L;

    private final Map<Player, BukkitTask> itemRetentionTasks =
        new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ItemStack ring = new ItemStack(Material.DIAMOND_SWORD);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(this, () -> {
            InventoryView view = player.getOpenInventory();

            // If the open inventory is a player inventory
            // Update to the ring item
            // This will update even when it is closed, but
            // it is a small price to pay IMO
            if (isPlayerCraftingInv(view)) {
                Inventory crafting = view.getTopInventory();
                crafting.setItem(1, ring);
            }
        }, 0L, INV_UPDATE_INTERVAL);

        this.itemRetentionTasks.put(player, task);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BukkitTask task = this.itemRetentionTasks.remove(player);
        if (task != null) {
            task.cancel();
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        InventoryView view = event.getView();

        // Remove the ring item in the matrix to prevent
        // players from duping them
        if (isPlayerCraftingInv(view)) {
            view.getTopInventory().clear();
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryView view = event.getView();

        // Don't allow players to remove anything from their
        // own crafting matrix
        // The view includes the player's entire inventory
        // as well, so check to make sure that the clicker
        // did not click on their own inventory
        if (isPlayerCraftingInv(view) &&
                event.getClickedInventory() != event.getWhoClicked().getInventory()) {
            if (event.getSlot() < 5) {
                event.setCancelled(true);
            }
        }
    }

    private static boolean isPlayerCraftingInv(InventoryView view) {
        return view.getTopInventory().getSize() == PLAYER_CRAFT_INV_SIZE;
    }
}
