package com.undeadlydev.UWinEffects.cosmetics;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class WinEffectDestroyIsland implements WinEffect, Cloneable {

    private static boolean loaded = false;
    private static int spawnLaterTick, amountTNT, perFuseAmount;
    private BukkitTask task;

    @Override
    public void loadCustoms(Main plugin, String path) {
        if (!loaded) {
            spawnLaterTick = plugin.getWineffect().getIntOrDefault(path + ".spawnLaterTick", 20);
            amountTNT = plugin.getWineffect().getIntOrDefault(path + ".amountTNT", 4);
            perFuseAmount = plugin.getWineffect().getIntOrDefault(path + ".perFuseAmount", 15);
            loaded = true;
        }
    }

    @Override
    public void start(Player p) {
        World world = p.getWorld();
        task = new BukkitRunnable() {
            public void run() {
                if (p == null || !p.isOnline() || !world.getName().equals(p.getWorld().getName())) {
                    stop();
                    return;
                }
                explode(p.getLocation());

            }
        }.runTaskTimer(Main.get(), spawnLaterTick, spawnLaterTick);
    }

    private void explode(Location loc) {
        loc.getWorld().strikeLightning(loc);
        int pa = perFuseAmount;
        for (int i = 0; i < amountTNT; i++) {
            TNTPrimed tnt = loc.getWorld().spawn(loc, TNTPrimed.class);
            tnt.setFuseTicks(pa);
            pa += perFuseAmount;
        }
    }

    @Override
    public void stop() {
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    public WinEffect clone() {
        return new WinEffectDestroyIsland();
    }
}