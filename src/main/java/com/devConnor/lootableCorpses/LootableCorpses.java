package com.devConnor.lootableCorpses;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.devConnor.lootableCorpses.listeners.CommandListener;
import com.devConnor.lootableCorpses.listeners.ConnectListener;
import com.devConnor.lootableCorpses.listeners.CorpseListener;
import com.devConnor.lootableCorpses.listeners.PacketListener;
import com.devConnor.lootableCorpses.managers.CorpseManager;
import com.devConnor.lootableCorpses.managers.PacketManager;
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

    private PacketListener packetListener;

    @Getter
    private boolean isPluginEnabled;

    @Getter
    private int corpseLifespanAfterInteraction;

    @Override
    public void onEnable() {
        ConfigManager.setupConfig(this);

        this.corpseManager = new CorpseManager(this);
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.packetListener = new PacketListener(this, this.protocolManager, this.corpseManager);
        PacketManager.loadPacketManager(this);

        if (ConfigManager.isCorpseLifespanAfterInteractionSet()) {
            this.corpseLifespanAfterInteraction = ConfigManager.getCorpseLifespanAfterInteractionMillis();
        }

        Bukkit.getPluginManager().registerEvents(new CorpseListener(this, corpseManager), this);
        Bukkit.getPluginManager().registerEvents(new ConnectListener(corpseManager), this);

        this.isPluginEnabled = !ConfigManager.isLootingDisabled();
        if (this.isPluginEnabled) {
            this.packetListener.createUseEntityPacketListener();
        }

        getCommand("lootablecorpses").setExecutor(new CommandListener(this, corpseManager));
    }

    public Collection<? extends Player> getPlayers() {
        return Bukkit.getOnlinePlayers();
    }

    public void toggle(boolean enable) {
        this.isPluginEnabled = enable;
    }
}
