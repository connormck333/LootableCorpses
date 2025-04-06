package com.devConnor.lootableCorpses.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.devConnor.lootableCorpses.LootableCorpses;
import com.devConnor.lootableCorpses.instances.CorpseEntity;
import com.devConnor.lootableCorpses.instances.CorpseGui;
import com.devConnor.lootableCorpses.listeners.AutoRemover;
import com.devConnor.lootableCorpses.utils.ConfigManager;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CorpseManager {

    private final LootableCorpses lootableCorpses;

    @Getter
    private final ArrayList<CorpseEntity> corpses;

    private final AutoRemover autoRemover;
    private final HashMap<UUID, CorpseGui> inventoriesOpen;

    @Getter
    private final boolean instantRespawnEnabled;

    @Getter
    private final boolean killOnLeave;

    private final List<String> blackListedWorlds;
    private final int CORPSE_LIFESPAN_MILLIS;
    private final boolean keepCorpsesAboveTheVoid;
    private final boolean dropInvOnDespawn;

    public CorpseManager(LootableCorpses lootableCorpses) {
        this.lootableCorpses = lootableCorpses;

        this.corpses = new ArrayList<>();
        this.autoRemover = new AutoRemover(lootableCorpses, this);
        this.inventoriesOpen = new HashMap<>();
        this.instantRespawnEnabled = ConfigManager.isInstantRespawnEnabled();
        this.killOnLeave = ConfigManager.shouldKillOnLeave();
        this.keepCorpsesAboveTheVoid = ConfigManager.isKeepCorpsesAboveTheVoid();
        this.blackListedWorlds = ConfigManager.getBlacklistedWorlds();
        this.dropInvOnDespawn = ConfigManager.shouldDropInvOnDespawn();

        this.CORPSE_LIFESPAN_MILLIS = ConfigManager.getCorpseLifespanMillis();

        this.autoRemover.start();
    }

    public void createCorpse(Player player, PlayerInventory inventory) {
        Location location = player.getLocation();
        if (keepCorpsesAboveTheVoid) {
            location.setY(Math.max(location.getY(), (player.getWorld().getMinHeight() + 0.5)));
        }
        corpses.add(new CorpseEntity(lootableCorpses, player.getUniqueId(), location, inventory));
    }

    public void removeArmorFromCorpse(int entityId, int slot) {
        CorpseEntity corpseEntity = getCorpseEntity(entityId);
        if (corpseEntity != null) {
            corpseEntity.removeArmor(slot);
        }
    }

    public void revealCorpsesToNewPlayer(Player player) {
        World playerWorld = player.getWorld();
        for (CorpseEntity corpse : corpses) {
            if (corpse.getLocation().getWorld() == playerWorld) {
                corpse.sendPacketToPlayer(player);
            }
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

        setCorpseRemoverTimer(corpseEntity);
    }

    public boolean isWorldBlacklisted(String worldName) {
        return blackListedWorlds.contains(worldName);
    }

    public void clear(boolean disregardTime) {
        ArrayList<CorpseEntity> corpseEntitiesToDestroy = new ArrayList<>();
        IntList entityIds = new IntArrayList();
        for (CorpseEntity corpseEntity : corpses) {
            if (disregardTime || (CORPSE_LIFESPAN_MILLIS != Integer.MIN_VALUE && (System.currentTimeMillis() - corpseEntity.getTimestamp()) >= CORPSE_LIFESPAN_MILLIS)) {
                corpseEntitiesToDestroy.add(corpseEntity);
                entityIds.add(corpseEntity.getId());
            }
        }

        destroyCorpses(corpseEntitiesToDestroy, entityIds);
    }

    public void destroyCorpse(CorpseEntity corpseEntity) {
        ArrayList<CorpseEntity> corpseList = new ArrayList<>();
        corpseList.add(corpseEntity);

        destroyCorpses(corpseList, new IntArrayList(new int[]{corpseEntity.getId()}));
    }

    private void destroyCorpses(ArrayList<CorpseEntity> corpseEntitiesToDestroy, IntList entityIds) {
        PacketContainer removeEntityPacket = lootableCorpses.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        removeEntityPacket.getModifier().write(0, entityIds);

        for (Player player : lootableCorpses.getPlayers()) {
            lootableCorpses.getProtocolManager().sendServerPacket(player, removeEntityPacket);
        }

        for (CorpseEntity corpseEntity : corpseEntitiesToDestroy) {
            corpses.remove(corpseEntity);
            if (dropInvOnDespawn) {
                corpseEntity.dropRemainingInventory();
            }
        }
    }

    private void setCorpseRemoverTimer(CorpseEntity corpseEntity) {
        int lifespan = lootableCorpses.getCorpseLifespanAfterInteraction();
        if (lifespan == 0) {
            destroyCorpse(corpseEntity);
        } else if (lifespan > 0) {
            Bukkit.getScheduler().runTaskLater(lootableCorpses, () -> destroyCorpse(corpseEntity), lifespan);
        }
    }
}
