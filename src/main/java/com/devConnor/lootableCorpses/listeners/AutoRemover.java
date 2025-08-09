package com.devConnor.lootableCorpses.listeners;

import com.devConnor.lootableCorpses.LootableCorpses;
import com.devConnor.lootableCorpses.managers.CorpseManager;
import com.devConnor.lootableCorpses.utils.XScheduler;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoRemover extends BukkitRunnable {

    private final CorpseManager corpseManager;
    private final XScheduler.IScheduler scheduler;

    public AutoRemover( CorpseManager corpseManager) {
        this.corpseManager = corpseManager;
        this.scheduler = XScheduler.get();
    }

    public void start() {
        scheduler.runTaskTimer(this, 0, 600);
    }

    @Override
    public void run() {
        corpseManager.clear(false);
    }
}
