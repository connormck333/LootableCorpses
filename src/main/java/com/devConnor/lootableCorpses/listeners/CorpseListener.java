package com.devConnor.lootableCorpses.listeners;

import com.devConnor.lootableCorpses.instances.CorpseGui;
import com.devConnor.lootableCorpses.managers.CorpseManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CorpseListener implements Listener {

    private final CorpseManager corpseManager;

    public CorpseListener(CorpseManager corpseManager) {
        this.corpseManager = corpseManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        corpseManager.createCorpse(player, player.getInventory());
        e.getDrops().clear();
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
}
