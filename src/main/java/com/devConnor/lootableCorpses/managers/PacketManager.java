package com.devConnor.lootableCorpses.managers;

import com.comphenix.protocol.events.PacketContainer;
import com.devConnor.lootableCorpses.LootableCorpses;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class PacketManager {

    private static LootableCorpses lootableCorpses;

    public static void loadPacketManager(LootableCorpses lootableCorpses) {
        PacketManager.lootableCorpses = lootableCorpses;
    }

    public static void sendPacketToServer(PacketContainer packet) {
        for (Player player : lootableCorpses.getPlayers()) {
            sendPacketToPlayer(player, packet);
        }
    }

    public static void sendPacketToPlayer(Player player, PacketContainer packet) {
        lootableCorpses.getProtocolManager().sendServerPacket(player, packet);
    }
}
