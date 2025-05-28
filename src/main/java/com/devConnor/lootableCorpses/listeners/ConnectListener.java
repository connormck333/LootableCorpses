package com.devConnor.lootableCorpses.listeners;

import com.devConnor.lootableCorpses.managers.CorpseManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectListener implements Listener {

    private final CorpseManager corpseManager;

    public ConnectListener(CorpseManager corpseManager) {
        this.corpseManager = corpseManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        this.corpseManager.revealCorpsesToNewPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (corpseManager.isKillOnLeaveEnabled()) {
            Player player = e.getPlayer();
            player.setHealth(0);
        } else if (corpseManager.isSpawnCorpseOnLeaveEnabled()) {
            Player player = e.getPlayer();
            corpseManager.createCorpse(player, player.getInventory());
            player.getInventory().clear();
        }
    }
}
