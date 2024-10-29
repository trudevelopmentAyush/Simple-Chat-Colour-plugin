
package com.example.chatcolour;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class ChatColourPlugin extends JavaPlugin implements CommandExecutor {

    private final Map<String, ChatColor> playerColours = new HashMap<>();
    private final Map<String, ChatColor> rankColours = new HashMap<>();
    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        this.getCommand("setcolour").setExecutor(this);
        this.luckPerms = LuckPermsProvider.get();
        Bukkit.getServer().getPluginManager().registerEvents(new ChatListener(this), this);
    }

    public ChatColor getPlayerColour(String playerName) {
        return playerColours.getOrDefault(playerName, ChatColor.WHITE);
    }

    public ChatColor getRankColour(String rankName) {
        return rankColours.getOrDefault(rankName, ChatColor.WHITE);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /setcolour player [player_name] [colour] or /setcolour rank [rank_name] [colour]");
            return false;
        }

        String type = args[0].toLowerCase();
        String targetName = args[1];
        ChatColor color;

        try {
            color = ChatColor.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Invalid color. Use a valid color name.");
            return false;
        }

        if (type.equals("player")) {
            playerColours.put(targetName, color);
            sender.sendMessage(ChatColor.GREEN + "Set chat colour for player " + targetName + " to " + color + ".");
        } else if (type.equals("rank")) {
            rankColours.put(targetName, color);
            sender.sendMessage(ChatColor.GREEN + "Set chat colour for rank " + targetName + " to " + color + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid type. Use 'player' or 'rank'.");
            return false;
        }

        return true;
    }

    public ChatColor getEffectiveColour(Player player) {
        String playerName = player.getName();
        if (playerColours.containsKey(playerName)) {
            return playerColours.get(playerName);
        }

        // Check rank color if player color isn't set
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            return user.getNodes().stream()
                    .filter(node -> node.getType() == net.luckperms.api.node.types.InheritanceNode.class)
                    .map(node -> ((net.luckperms.api.node.types.InheritanceNode) node).getGroupName())
                    .map(rankColours::get)
                    .filter(java.util.Objects::nonNull)
                    .findFirst()
                    .orElse(ChatColor.WHITE);
        }

        return ChatColor.WHITE;
    }
}
