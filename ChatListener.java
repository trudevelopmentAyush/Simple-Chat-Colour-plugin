
package com.example.chatcolour;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final ChatColourPlugin plugin;

    public ChatListener(ChatColourPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ChatColor colour = plugin.getEffectiveColour(player);
        event.setFormat(colour + player.getName() + ChatColor.RESET + ": " + event.getMessage());
    }
}
