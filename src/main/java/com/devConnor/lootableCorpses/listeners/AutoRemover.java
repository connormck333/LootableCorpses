package com.devConnor.lootableCorpses.listeners;

import com.devConnor.lootableCorpses.LootableCorpses;
import com.devConnor.lootableCorpses.managers.CorpseManager;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoRemover extends BukkitRunnable {

    private final LootableCorpses lootableCorpses;
    private final CorpseManager corpseManager;

    public AutoRemover(LootableCorpses lootableCorpses, CorpseManager corpseManager) {
        this.lootableCorpses = lootableCorpses;
        this.corpseManager = corpseManager;
    }

    public void start() {
        runTaskTimer(lootableCorpses, 0, 600);
    }

    @Override
    public void run() {
        corpseManager.clear(false);
    }
}
