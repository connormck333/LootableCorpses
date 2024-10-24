package com.devConnor.lootableCorpses.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.devConnor.lootableCorpses.LootableCorpses;
import com.devConnor.lootableCorpses.instances.corpses.CorpseEntity;
import com.devConnor.lootableCorpses.instances.CorpseGui;
import com.devConnor.lootableCorpses.listeners.AutoRemover;
import com.devConnor.lootableCorpses.utils.ConfigManager;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
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

    private final int CORPSE_LIFESPAN_MILLIS;

    public CorpseManager(LootableCorpses lootableCorpses) {
        this.lootableCorpses = lootableCorpses;

        this.corpses = new ArrayList<>();
        this.autoRemover = new AutoRemover(lootableCorpses, this);
        this.inventoriesOpen = new HashMap<>();

        this.CORPSE_LIFESPAN_MILLIS = ConfigManager.getCorpseLifespanMillis();

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

    public void createNewCorpseGui(Player player, CorpseEntity corpseEntity) {
        CorpseGui gui = corpseEntity.getCorpseGui();
        inventoriesOpen.put(player.getUniqueId(), gui);
        gui.open(player);
    }

    public void clear(boolean disregardTime) {
        ArrayList<CorpseEntity> corpseEntitiesToDestroy = new ArrayList<>();
        IntList entityIds = new IntArrayList();
        for (CorpseEntity corpseEntity : corpses) {
            if (disregardTime || (System.currentTimeMillis() - corpseEntity.getTimestamp()) >= CORPSE_LIFESPAN_MILLIS) {
                corpseEntitiesToDestroy.add(corpseEntity);
                entityIds.add(corpseEntity.getId());
            }
        }

        destroyCorpses(corpseEntitiesToDestroy, entityIds);
    }

    public void destroyCorpse(CorpseEntity corpseEntity) {
        ArrayList<CorpseEntity> corpseList = new ArrayList<>();
        corpseList.add(corpseEntity);

        destroyCorpses(corpseList, new IntArrayList(new int[] { corpseEntity.getId() }));
    }

    private void destroyCorpses(ArrayList<CorpseEntity> corpseEntitiesToDestroy, IntList entityIds) {
        PacketContainer removeEntityPacket = lootableCorpses.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        removeEntityPacket.getModifier().write(0, entityIds);

        for (Player player : lootableCorpses.getPlayers()) {
            lootableCorpses.getProtocolManager().sendServerPacket(player, removeEntityPacket);
        }

        for (CorpseEntity corpseEntity : corpseEntitiesToDestroy) {
            corpses.remove(corpseEntity);
        }
    }
}
