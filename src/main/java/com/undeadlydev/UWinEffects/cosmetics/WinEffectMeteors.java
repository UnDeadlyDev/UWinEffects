package com.undeadlydev.UWinEffects.cosmetics;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class WinEffectMeteors implements WinEffect, Cloneable {

    private static boolean loaded = false;
    private static int  taskTick;
    private static double maxOfCenter;
    // Add a new static variable for the height above the player
    private static double heightAbovePlayer; // New variable

    private BukkitTask task;

    public WinEffectMeteors() {
        this.task = null;
    }

    @Override
    public void loadCustoms(Main plugin, String path) {
        if (!loaded) {
            maxOfCenter = plugin.getWineffect().getDoubleOrDefault(path + ".maxOfCenter", 1);
            taskTick = plugin.getWineffect().getIntOrDefault(path + ".taskTick", 2);
            heightAbovePlayer = plugin.getWineffect().getDoubleOrDefault(path + ".heightAbovePlayer", 10.0); // Default to 10 blocks
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
                Location playerLoc = p.getLocation();
                Location spawnLoc = playerLoc.clone().add(0, heightAbovePlayer, 0);

                Fireball fb = p.getWorld().spawn(spawnLoc, Fireball.class); // Spawn in player's world
                fb.setVelocity(new Vector(ThreadLocalRandom.current().nextDouble(-maxOfCenter, maxOfCenter), -1.5, ThreadLocalRandom.current().nextDouble(-maxOfCenter, maxOfCenter)));
            }
        }.runTaskTimer(Main.get(), taskTick, taskTick);
    }

    @Override
    public void stop() {
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    public WinEffect clone() {
        return new WinEffectMeteors();
    }
}