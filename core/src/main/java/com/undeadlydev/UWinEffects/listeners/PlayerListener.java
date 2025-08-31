package com.undeadlydev.UWinEffects.listeners;

import com.cryptomorin.xseries.XAttribute;
import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.enums.Permission;
import com.undeadlydev.UWinEffects.events.PlayerLoadEvent;
import com.undeadlydev.UWinEffects.superclass.SpigotUpdater;
import com.undeadlydev.UWinEffects.utils.ChatUtils;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
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
        removeEffect(p);
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent e) {
        Player p = e.getPlayer();
        plugin.getDb().savePlayerRemove(p);
        removeEffect(p);
    }

    private void removeEffect(Player player) {
        if (plugin.getCos().winEffectsTask.containsKey(player.getUniqueId())) {
            plugin.getCos().winEffectsTask.remove(player.getUniqueId()).stop();
        }
    }
    @EventHandler
    public void PlayerJoinUpdateCheck(PlayerJoinEvent e) {
        if (plugin.getConfig().getBoolean("update-check")) {
            Player player = e.getPlayer();
            if (player.isOp() || player.hasPermission(Permission.ADMIN.get()) || player.hasPermission(Permission.UPDATE_CHECK.get())) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        SpigotUpdater updater = new SpigotUpdater(plugin, plugin.getResourceId());
                        try {
                            if (updater.checkForUpdates()) {
                                String message = plugin.getLang().get("messages.notifyUpdate")
                                        .replace("{CURRENT}", plugin.getDescription().getVersion())
                                        .replace("{NEW}", updater.getLatestVersion())
                                        .replace("{LINK}", updater.getResourceURL());
                                player.sendMessage(ChatUtils.colorCodes(message));
                            }
                        } catch (Exception e) {
                        }
                    }
                }.runTaskAsynchronously(plugin);
            }
        }
    }
}