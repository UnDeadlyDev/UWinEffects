package com.undeadlydev.UWinEffects.listeners;

import com.cryptomorin.xseries.XAttribute;
import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.events.PlayerLoadEvent;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener {

    private Main plugin;

    public PlayerListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void loadPlayer(PlayerLoadEvent e) {
        Player p = e.getPlayer();
        AttributeInstance scaleAttribute = p.getAttribute(XAttribute.SCALE.get());
        if (p == null || !p.isOnline())
            return;

        scaleAttribute.setBaseValue(1.0);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        plugin.getDb().loadPlayer(p);

    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player p = e.getPlayer();
        plugin.getDb().savePlayerRemove(p);
        plugin.getUim().removeInventory(p);
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent e) {
        Player p = e.getPlayer();
        plugin.getDb().savePlayerRemove(p);
        plugin.getUim().removeInventory(p);
    }
}