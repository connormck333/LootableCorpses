package com.devConnor.lootableCorpses.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtils {

    public static void sendRedMessage(Player player, String message) {
        player.sendMessage(ChatColor.RED + message);
    }

    public static void sendRedMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.RED + message);
    }

    public static void sendGreenMessage(Player player, String message) {
        player.sendMessage(ChatColor.GREEN + message);
    }

    public static void sendGreenMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.GREEN + message);
    }
}
