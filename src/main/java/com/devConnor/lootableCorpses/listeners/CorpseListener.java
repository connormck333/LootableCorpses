package com.devConnor.lootableCorpses.listeners;

import com.devConnor.lootableCorpses.LootableCorpses;
import com.devConnor.lootableCorpses.instances.CorpseGui;
import com.devConnor.lootableCorpses.managers.CorpseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CorpseListener implements Listener {

    private final LootableCorpses lootableCorpses;
    private final CorpseManager corpseManager;

    public CorpseListener(LootableCorpses lootableCorpses, CorpseManager corpseManager) {
        this.lootableCorpses = lootableCorpses;
        this.corpseManager = corpseManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (lootableCorpses.isPluginEnabled()) {
            if(e.getKeepInventory()) return;
            Player player = e.getEntity();
            PlayerInventory inventory = player.getInventory();
            List<ItemStack> drops = e.getDrops();
            Set<ItemStack> dropSet = new HashSet<>(drops);
            drops.clear();
            for (int i = 0; i <= 40; i++) {
                if (!dropSet.contains(inventory.getItem(i))) {
                    inventory.clear(i);
                }
            }
            corpseManager.createCorpse(player, inventory);

            if (corpseManager.isInstantRespawnEnabled()) {
                Bukkit.getScheduler().runTaskLater(lootableCorpses, () -> player.spigot().respawn(), 0L);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) {
            return;
        }

        CorpseGui inventory = corpseManager.getPlayerOpenGui(player.getUniqueId());
        if (inventory == null) {
            return;
        }

        int slot = e.getRawSlot();
        if (slot >= 36 && slot <= 39) {
            corpseManager.removeArmorFromCorpse(inventory.getEntityId(), slot);
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e) {
        if (lootableCorpses.isPluginEnabled()) {
            Player player = e.getPlayer();
            corpseManager.revealCorpsesToNewPlayer(player);
        }
    }
}
