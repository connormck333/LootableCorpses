package com.devConnor.lootableCorpses.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.devConnor.lootableCorpses.LootableCorpses;
import com.devConnor.lootableCorpses.instances.CorpseEntity;
import com.devConnor.lootableCorpses.instances.CorpseRemoveWand;
import com.devConnor.lootableCorpses.managers.CorpseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PacketListener {

    private final LootableCorpses lootableCorpses;
    private final ProtocolManager protocolManager;
    private final CorpseManager corpseManager;

    public PacketListener(LootableCorpses lootableCorpses, ProtocolManager protocolManager, CorpseManager corpseManager) {
        this.lootableCorpses = lootableCorpses;
        this.protocolManager = protocolManager;
        this.corpseManager = corpseManager;
    }

    public void createUseEntityPacketListener() {
        protocolManager.addPacketListener(new PacketAdapter(this.lootableCorpses, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent e) {
                PacketContainer packet = e.getPacket();
                int entityId = packet.getIntegers().read(0);

                CorpseEntity corpseEntity = corpseManager.getCorpseEntity(entityId);
                Player player = e.getPlayer();
                if (corpseEntity == null || player == null) {
                    return;
                }

                if (CorpseRemoveWand.isWand(player.getInventory().getItemInMainHand())) {
                    Bukkit.getScheduler().runTask(lootableCorpses, () -> corpseManager.destroyCorpse(corpseEntity));
                    return;
                }

                Bukkit.getScheduler().runTask(lootableCorpses, () -> corpseManager.createNewCorpseGui(player, corpseEntity));
            }
        });
    }
}
