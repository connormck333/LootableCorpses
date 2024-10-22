package com.devConnor.lootableCorpses.instances;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@Getter
public class Armor {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;

    public Armor(PlayerInventory inventory) {
        this.helmet = inventory.getHelmet();
        this.chestplate = inventory.getChestplate();
        this.leggings = inventory.getLeggings();
        this.boots = inventory.getBoots();
    }

    public ItemStack[] getArmor() {
        return new ItemStack[] { this.boots, this.leggings, this.chestplate, this.helmet };
    }

    public void removeArmor(int slot) {
        ItemStack air = new ItemStack(Material.AIR);
        switch (slot) {
            case 36: this.boots = air;
            case 37: this.leggings = air;
            case 38: this.chestplate = air;
            case 39: this.helmet = air;
        }
    }
}
