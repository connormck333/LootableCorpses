package com.devConnor.lootableCorpses.instances;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static com.devConnor.lootableCorpses.utils.ConfigManager.getInventoryTitle;

public class CorpseGui {

    private final Inventory gui;

    @Getter
    private final int entityId;

    public CorpseGui(int entityId, UUID corpseUUID, CorpseInventory corpseInventory) {
        this.entityId = entityId;

        final int INVENTORY_SIZE = 45;
        Player corpsePlayer = Bukkit.getPlayer(corpseUUID);

        if (corpsePlayer == null) {
            this.gui = Bukkit.createInventory(null, INVENTORY_SIZE, "Player Inventory");
        } else {
            this.gui = Bukkit.createInventory(null, INVENTORY_SIZE, getInventoryTitle().replace("{player}", corpsePlayer.getName()));
        }

        setup(corpseInventory);
    }

    private void setup(CorpseInventory corpseInventory) {
        ItemStack[] items = corpseInventory.getInventory();
        for (int i = 0; i < items.length; i++) {
            gui.setItem(i, items[i]);
        }
    }

    public void open(Player player) {
        if (player != null) {
            player.openInventory(gui);
        }
    }
}
