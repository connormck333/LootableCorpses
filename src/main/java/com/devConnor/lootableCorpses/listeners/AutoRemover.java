package com.devConnor.lootableCorpses.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.devConnor.lootableCorpses.LootableCorpses;
import com.devConnor.lootableCorpses.instances.CorpseEntity;
import com.devConnor.lootableCorpses.managers.CorpseManager;
import com.devConnor.lootableCorpses.utils.ConfigManager;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class AutoRemover extends BukkitRunnable {

    private final LootableCorpses lootableCorpses;
    private final CorpseManager corpseManager;
    private final int CORPSE_LIFESPAN_MILLIS;

    public AutoRemover(LootableCorpses lootableCorpses, CorpseManager corpseManager) {
        this.lootableCorpses = lootableCorpses;
        this.corpseManager = corpseManager;
        this.CORPSE_LIFESPAN_MILLIS = ConfigManager.getCorpseLifespanMillis();
    }

    public void start() {
        runTaskTimer(lootableCorpses, 0, 600);
    }

    @Override
    public void run() {
        ArrayList<CorpseEntity> corpseEntitiesToDestroy = new ArrayList<>();
        IntList entityIds = new IntArrayList();
        for (CorpseEntity corpseEntity : corpseManager.getCorpses()) {
            if ((System.currentTimeMillis() - corpseEntity.getTimestamp()) >= CORPSE_LIFESPAN_MILLIS) {
                corpseEntitiesToDestroy.add(corpseEntity);
                entityIds.add(corpseEntity.getId());
            }
        }

        destroyCorpses(corpseEntitiesToDestroy, entityIds);
    }

    private void destroyCorpses(ArrayList<CorpseEntity> corpseEntitiesToDestroy, IntList entityIds) {
        PacketContainer removeEntityPacket = lootableCorpses.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        removeEntityPacket.getModifier().write(0, entityIds);

        for (Player player : lootableCorpses.getPlayers()) {
            lootableCorpses.getProtocolManager().sendServerPacket(player, removeEntityPacket);
        }

        for (CorpseEntity corpseEntity : corpseEntitiesToDestroy) {
            corpseManager.deleteCorpse(corpseEntity);
        }
    }
}
