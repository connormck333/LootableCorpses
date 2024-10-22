package com.devConnor.lootableCorpses.utils;

import com.devConnor.lootableCorpses.LootableCorpses;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private static LootableCorpses lootableCorpses;
    private static FileConfiguration config;

    public static void setupConfig(LootableCorpses lootableCorpses) {
        ConfigManager.lootableCorpses = lootableCorpses;
        ConfigManager.config = lootableCorpses.getConfig();

        lootableCorpses.saveDefaultConfig();
    }

}
