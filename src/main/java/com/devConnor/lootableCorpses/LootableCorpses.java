package com.devConnor.lootableCorpses;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.devConnor.lootableCorpses.instances.corpses.CorpseEntity;
import com.devConnor.lootableCorpses.instances.CorpseRemoveWand;
import com.devConnor.lootableCorpses.listeners.CommandListener;
import com.devConnor.lootableCorpses.listeners.CorpseListener;
import com.devConnor.lootableCorpses.managers.CorpseManager;
import com.devConnor.lootableCorpses.utils.ConfigManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public final class LootableCorpses extends JavaPlugin {

    private CorpseManager corpseManager;

    @Getter
    private ProtocolManager protocolManager;

    @Getter
    private boolean isPluginEnabled;

    @Override
    public void onEnable() {
        ConfigManager.setupConfig(this);

        this.corpseManager = new CorpseManager(this);
        this.protocolManager = ProtocolLibrary.getProtocolManager();

        Bukkit.getPluginManager().registerEvents(new CorpseListener(this, corpseManager), this);

        this.isPluginEnabled = !ConfigManager.isLootingDisabled();
        if (this.isPluginEnabled) {
            createUseEntityPacketListener();
        }

        getCommand("lootablecorpses").setExecutor(new CommandListener(this, corpseManager));
    }

    public Collection<? extends Player> getPlayers() {
        return Bukkit.getOnlinePlayers();
    }

    public void toggle(boolean enable) {
        this.isPluginEnabled = enable;
    }

    private void createUseEntityPacketListener() {
        protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Play.Client.USE_ENTITY) {
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
                    Bukkit.getScheduler().runTask(LootableCorpses.this, () -> corpseManager.destroyCorpse(corpseEntity));
                    return;
                }

                Bukkit.getScheduler().runTask(LootableCorpses.this, () -> corpseManager.createNewCorpseGui(player, corpseEntity));
            }
        });
    }
}
