package com.devConnor.lootableCorpses.instances;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.devConnor.lootableCorpses.LootableCorpses;
import lombok.Getter;
import net.minecraft.world.entity.EntityPose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CorpseEntity {
    
    private final LootableCorpses lootableCorpses;

    @Getter
    private final int id;

    @Getter
    private final UUID player;

    @Getter
    private final Location location;

    @Getter
    private final CorpseInventory corpseInventory;

    @Getter
    private final CorpseGui corpseGui;

    @Getter
    private final Long timestamp;

    private final EntityPose pose;
    private WrappedGameProfile corpse;

    private final ArrayList<PacketContainer> packets;
    private final EnumWrappers.ItemSlot[] armorSlots;

    public CorpseEntity(LootableCorpses lootableCorpses, UUID player, Location deathLocation, PlayerInventory inventory) {
        this.lootableCorpses = lootableCorpses;

        this.id = (int) (Math.random() * Integer.MAX_VALUE);
        this.player = player;
        this.location = deathLocation;
        this.pose = EntityPose.b;
        this.corpseInventory = new CorpseInventory(inventory);
        this.corpseGui = new CorpseGui(this.id, player, this.corpseInventory);
        this.packets = new ArrayList<>();

        this.packets.add(createCorpse());
        this.packets.add(spawnCorpse());
        this.packets.add(getMetadataPacket());

        this.armorSlots = new EnumWrappers.ItemSlot[]{
                EnumWrappers.ItemSlot.FEET,
                EnumWrappers.ItemSlot.LEGS,
                EnumWrappers.ItemSlot.CHEST,
                EnumWrappers.ItemSlot.HEAD
        };
        this.timestamp = System.currentTimeMillis();

        sendPackets();
    }

    private PacketContainer createCorpse() {
        Player corpsePlayer =  Bukkit.getPlayer(player);
        if (corpsePlayer == null) {
            return null;
        }

        WrappedGameProfile corpseProfile = WrappedGameProfile.fromPlayer(corpsePlayer);
        WrappedSignedProperty textures = corpseProfile.getProperties().get("textures").stream().findFirst().orElse(null);

        this.corpse = new WrappedGameProfile(UUID.randomUUID(), corpsePlayer.getName());
        if (textures != null) {
            corpse.getProperties().put("textures", new WrappedSignedProperty("textures", textures.getValue(), textures.getSignature()));
        }

        PlayerInfoData playerInfoData = new PlayerInfoData(
                corpse,
                0,
                EnumWrappers.NativeGameMode.SURVIVAL,
                null
        );

        PacketContainer playerInfoPacket = lootableCorpses.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        playerInfoPacket.getPlayerInfoActions().write(0, Collections.singleton(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
        playerInfoPacket.getPlayerInfoDataLists().write(1, Collections.singletonList(playerInfoData));

        return playerInfoPacket;
    }

    private PacketContainer spawnCorpse() {
        PacketContainer spawnEntityPacket = lootableCorpses.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        spawnEntityPacket.getUUIDs().write(0, corpse.getUUID());
        spawnEntityPacket.getIntegers().write(0, this.id);

        Location deathLocation = location;
        spawnEntityPacket.getDoubles()
                .write(0, deathLocation.getX())
                .write(1, getHighestBlock())
                .write(2, deathLocation.getZ());
        spawnEntityPacket.getBytes()
                .write(0, (byte) ((deathLocation.getYaw() * 256.0F) / 360.0F))
                .write(1, (byte) -90);
        spawnEntityPacket.getEntityTypeModifier().write(0, EntityType.PLAYER);

        return spawnEntityPacket;
    }

    private PacketContainer getMetadataPacket() {
        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(6, WrappedDataWatcher.Registry.get(EntityPose.class)), this.pose);

        // Prepare the metadata packet
        PacketContainer metadataPacket = lootableCorpses.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        List<WrappedDataValue> wrappedDataValues = dataWatcher.getWatchableObjects().stream()
                .map(watchableObject -> new WrappedDataValue(watchableObject.getIndex(), watchableObject.getWatcherObject().getSerializer(), watchableObject.getValue()))
                .collect(Collectors.toList());

        // Write the entity ID and data
        metadataPacket.getIntegers().write(0, this.id);
        metadataPacket.getDataValueCollectionModifier().write(0, wrappedDataValues);

        return metadataPacket;
    }

    private PacketContainer getArmorPacket(EnumWrappers.ItemSlot slot, ItemStack item) {
        PacketContainer packet = lootableCorpses.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);

        packet.getIntegers().write(0, this.id);
        Pair<EnumWrappers.ItemSlot, ItemStack> pair = new Pair<>(slot, item);
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipmentList = List.of(pair);
        packet.getSlotStackPairLists().write(0, equipmentList);

        return packet;
    }

    private double getHighestBlock() {
        Location highestBlockLocation = location.getWorld().getHighestBlockAt(location).getLocation();

        return highestBlockLocation.getBlockY() < location.getBlockY() ? highestBlockLocation.getBlockY() + 0.9 : location.getBlockY() - 0.1;
    }

    private void sendPackets() {
        for (Player p: lootableCorpses.getPlayers()) {
            sendPacketToPlayer(p);
        }
    }

    private void sendArmorPacket(Player p) {
        for (int i = 0; i < armorSlots.length; i++) {
            lootableCorpses.getProtocolManager().sendServerPacket(p, getArmorPacket(armorSlots[i], corpseInventory.getArmor().getArmor()[i]));
        }
    }

    public void sendPacketToPlayer(Player p) {
        try {
            for (PacketContainer packet : packets) {
                lootableCorpses.getProtocolManager().sendServerPacket(p, packet);
            }
            sendArmorPacket(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeArmor(int slot) {
        corpseInventory.getArmor().removeArmor(slot);
        for (Player p : lootableCorpses.getPlayers()) {
            sendArmorPacket(p);
        }
    }
}
