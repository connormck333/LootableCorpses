package com.devConnor.lootableCorpses.listeners;

import com.devConnor.lootableCorpses.managers.CorpseManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ConnectListener implements Listener {

    private final CorpseManager corpseManager;

    public ConnectListener(CorpseManager corpseManager) {
        this.corpseManager = corpseManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        this.corpseManager.revealCorpsesToNewPlayer(e.getPlayer());
    }
}
