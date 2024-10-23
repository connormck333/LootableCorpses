package com.devConnor.lootableCorpses.instances;

import com.devConnor.lootableCorpses.managers.CorpseManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class CorpseRemoveWand {

    private final CorpseManager corpseManager;
    private final ItemStack wand;

    private static final String WAND_TITLE = ChatColor.RED + ChatColor.BOLD.toString() + "Corpse Remover";
    private static final Material ITEM_TYPE = Material.WOODEN_AXE;

    public CorpseRemoveWand(CorpseManager corpseManager) {
        this.corpseManager = corpseManager;
        this.wand = createWand();
    }

    public void givePlayerWand(Player player) {
        player.getInventory().addItem(wand);
    }

    public static boolean isWand(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        String displayName = item.getItemMeta().getDisplayName();
        Material itemType = item.getType();

        return displayName.equals(WAND_TITLE) && itemType == ITEM_TYPE;
    }

    private ItemStack createWand() {
        ItemStack item = new ItemStack(ITEM_TYPE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(WAND_TITLE);
        meta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "Click a corpse to remove it"));
        item.setItemMeta(meta);

        return item;
    }
}
