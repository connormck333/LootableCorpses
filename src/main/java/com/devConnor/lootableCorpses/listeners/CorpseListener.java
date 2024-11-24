package com.devConnor.lootableCorpses.listeners;

import com.devConnor.lootableCorpses.LootableCorpses;
import com.devConnor.lootableCorpses.instances.CorpseGui;
import com.devConnor.lootableCorpses.managers.CorpseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class CorpseListener implements Listener {

    private final LootableCorpses lootableCorpses;
    private final CorpseManager corpseManager;

    public CorpseListener(LootableCorpses lootableCorpses, CorpseManager corpseManager) {
        this.lootableCorpses = lootableCorpses;
        this.corpseManager = corpseManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (lootableCorpses.isPluginEnabled()) {
            Player player = e.getEntity();
            corpseManager.createCorpse(player, player.getInventory());
            e.getDrops().clear();

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
