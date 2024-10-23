package com.devConnor.lootableCorpses.managers;

import com.devConnor.lootableCorpses.LootableCorpses;
import com.devConnor.lootableCorpses.instances.CorpseEntity;
import com.devConnor.lootableCorpses.instances.CorpseGui;
import com.devConnor.lootableCorpses.listeners.AutoRemover;
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

    private final AutoRemover autoRemover;
    private final HashMap<UUID, CorpseGui> inventoriesOpen;

    public CorpseManager(LootableCorpses lootableCorpses) {
        this.lootableCorpses = lootableCorpses;

        this.corpses = new ArrayList<>();
        this.autoRemover = new AutoRemover(lootableCorpses, this);
        this.inventoriesOpen = new HashMap<>();

        this.autoRemover.start();
    }

    public void createCorpse(Player player, PlayerInventory inventory) {
        corpses.add(new CorpseEntity(lootableCorpses, player.getUniqueId(), player.getLocation(), inventory));
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

    public void deleteCorpse(CorpseEntity corpseEntity) {
        corpses.remove(corpseEntity);
    }

    public void createNewCorpseGui(Player player, CorpseEntity corpseEntity) {
        CorpseGui gui = corpseEntity.getCorpseGui();
        inventoriesOpen.put(player.getUniqueId(), gui);
        gui.open(player);
    }
}
