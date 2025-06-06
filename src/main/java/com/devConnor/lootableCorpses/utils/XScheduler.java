package com.devConnor.lootableCorpses.utils;


import com.devConnor.lootableCorpses.LootableCorpses;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import org.bukkit.scheduler.BukkitScheduler;

public class XScheduler {
    private static XScheduler instance;
    private IScheduler scheduler;

    public XScheduler(LootableCorpses lootableCorpses) {
        instance = this;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            this.scheduler = new FoliaScheduler(lootableCorpses);
        } catch (ClassNotFoundException e) {
            this.scheduler = new CommonScheduler(lootableCorpses);
        }
    }

    public static IScheduler get() {
        return instance.scheduler;
    }

    public interface IScheduler {
        void cancelAll();

        void runTaskLater(Runnable task, long delay);

        void runTask(Runnable task);

        void runTaskTimer(Runnable task, long delay, long period);

    }

    private static class CommonScheduler implements IScheduler {
        private final LootableCorpses plugin;
        private final BukkitScheduler scheduler;

        public CommonScheduler(LootableCorpses lootableCorpses) {
            this.plugin = lootableCorpses;
            this.scheduler = plugin.getServer().getScheduler();
        }

        @Override
        public void cancelAll() {
            scheduler.cancelTasks(plugin);
        }

        @Override
        public void runTaskLater(Runnable task, long delay) {
            if (delay <= 0) {
                runTask(task);
                return;
            }
            scheduler.runTaskLater(plugin, task, delay);
        }

        @Override
        public void runTask(Runnable task) {
            scheduler.runTask(plugin, task);
        }

        @Override
        public void runTaskTimer(Runnable task, long delay, long period) {
            scheduler.runTaskTimer(plugin, task, delay, period);
        }

    }

    private static class FoliaScheduler implements IScheduler {
        private final LootableCorpses plugin;
        private final GlobalRegionScheduler scheduler;

        public FoliaScheduler(LootableCorpses lootableCorpses) {
            this.plugin = lootableCorpses;
            this.scheduler = plugin.getServer().getGlobalRegionScheduler();
        }

        @Override
        public void cancelAll() {
            scheduler.cancelTasks(plugin);
        }

        @Override
        public void runTaskLater(Runnable task, long delay) {
            if (delay <= 0) {
                runTask(task);
                return;
            }
            scheduler.runDelayed(plugin, (plugin) -> task.run(), delay);
        }

        @Override
        public void runTask(Runnable task) {
            scheduler.run(plugin, (plugin) -> task.run());
        }

        @Override
        public void runTaskTimer(Runnable task, long delay, long period) {
            scheduler.runAtFixedRate(plugin, (plugin) -> task.run(), delay, period);
        }

    }

}
