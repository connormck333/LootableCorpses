package com.devConnor.lootableCorpses.instances;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@Getter
public class CorpseInventory {

    private final ItemStack[] inventory;
    private final Armor armor;
    private final ItemStack offHand;

    public CorpseInventory(PlayerInventory inventory) {
        this.inventory = cloneContents(inventory.getContents());
        this.armor = new Armor(inventory);
        this.offHand = inventory.getItemInOffHand();
    }

    private ItemStack[] cloneContents(ItemStack[] items) {
        ItemStack[] clone = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            clone[i] = (items[i] != null) ? items[i].clone() : null;
        }
        return clone;
    }
}
