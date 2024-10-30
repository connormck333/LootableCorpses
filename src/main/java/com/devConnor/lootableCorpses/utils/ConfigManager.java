package com.devConnor.lootableCorpses.utils;

import com.devConnor.lootableCorpses.LootableCorpses;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private static FileConfiguration config;

    public static void setupConfig(LootableCorpses lootableCorpses) {
        ConfigManager.config = lootableCorpses.getConfig();

        lootableCorpses.saveDefaultConfig();
    }

    public static int getCorpseLifespanMillis() {
        return (config.getInt("remove-corpse-after-minutes") * 60) * 1000;
    }

    public static boolean isLootingDisabled() {
        return config.getBoolean("disable-looting");
    }
}
