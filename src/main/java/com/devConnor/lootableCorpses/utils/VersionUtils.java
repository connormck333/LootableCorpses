package com.devConnor.lootableCorpses.utils;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionUtils {

    public static boolean isVersionAtLeast(String versionToCheck) {
        String currentVersion = Bukkit.getVersion();

        // Extract major and minor versions from the current version
        String regex = "MC: 1\\.(\\d+)(?:\\.(\\d+))?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(currentVersion);

        if (matcher.find()) {
            int currentMajor = Integer.parseInt(matcher.group(1));
            int currentMinor = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;

            // Extract major and minor from the versionToCheck
            String[] versionParts = versionToCheck.split("\\.");
            if (versionParts.length < 2) {
                return false; // Invalid version to check
            }

            int checkMajor = Integer.parseInt(versionParts[0]);
            int checkMinor = Integer.parseInt(versionParts[1]);

            // Compare versions: check if the current version is at least the version to check
            return (currentMajor > checkMajor) || (currentMajor == checkMajor && currentMinor >= checkMinor);
        }

        return false;
    }

}
