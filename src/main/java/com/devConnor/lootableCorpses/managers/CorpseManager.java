package com.devConnor.lootableCorpses.managers;

import com.devConnor.lootableCorpses.LootableCorpses;
import com.devConnor.lootableCorpses.instances.CorpseEntity;
import com.devConnor.lootableCorpses.instances.CorpseGui;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CorpseManager {

    private final LootableCorpses lootableCorpses;

    @Getter
    private final ArrayList<CorpseEntity> corpses;

    private final HashMap<UUID, CorpseGui> inventoriesOpen;

    public CorpseManager(LootableCorpses lootableCorpses) {
        this.lootableCorpses = lootableCorpses;

        this.corpses = new ArrayList<>();
        this.inventoriesOpen = new HashMap<>();
    }

    public void createCorpse(Player player, PlayerInventory inventory) {
        corpses.add(new CorpseEntity(lootableCorpses, player.getUniqueId(), player.getLastDeathLocation(), inventory));
    }

    public void removeArmorFromCorpse(int entityId, int slot) {
        CorpseEntity corpseEntity = getCorpseEntity(entityId);
        if (corpseEntity != null) {
            corpseEntity.removeArmor(slot);
        }
    }

    public void revealCorpsesToNewPlayer(Player player) {
        for (CorpseEntity corpse : corpses) {
            corpse.sendPacketToPlayer(player);
        }
    }

    public CorpseEntity getCorpseEntity(int entityId) {
        for (CorpseEntity corpse : corpses) {
            if (corpse.getId() == entityId) {
                return corpse;
            }
        }

        return null;
    }

    public CorpseGui getPlayerOpenGui(UUID player) {
        return inventoriesOpen.get(player);
    }

    public Runnable createNewCorpseGui(Player player, CorpseEntity corpseEntity) {
        CorpseGui gui = corpseEntity.getCorpseGui();
        inventoriesOpen.put(player.getUniqueId(), gui);
        gui.open(player);
        return null;
    }
}
