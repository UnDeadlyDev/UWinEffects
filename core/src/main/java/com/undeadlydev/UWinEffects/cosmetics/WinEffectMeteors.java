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

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class WinEffectMeteors implements WinEffect, Cloneable {

    private static int taskTick;
    private static double maxOfCenter;
    private static double heightAbovePlayer;
    private BukkitTask task;
    private final ArrayList<Fireball> fireballs = new ArrayList<>();

    public WinEffectMeteors() {
        this.task = null;
    }

    @Override
    public void loadCustoms(Main plugin, String path) {
        maxOfCenter = plugin.getWineffect().getDoubleOrDefault(path + ".maxOfCenter", 1);
        taskTick = plugin.getWineffect().getIntOrDefault(path + ".taskTick", 2);
        heightAbovePlayer = plugin.getWineffect().getDoubleOrDefault(path + ".heightAbovePlayer", 10.0);
    }

    @Override
    public void start(Player p) {
        World world = p.getWorld();
        task = new BukkitRunnable() {
            public void run() {
                if (p == null || !p.isOnline() || !world.getName().equals(p.getWorld().getName())) {
                    Main.get().getCos().winEffectsTask.remove(p.getUniqueId()).stop();
                    return;
                }
                Location playerLoc = p.getLocation();
                Location spawnLoc = playerLoc.clone().add(0, heightAbovePlayer, 0);
                Fireball fb = world.spawn(spawnLoc, Fireball.class);
                fb.setIsIncendiary(false); // Prevent fire spread
                fb.setYield(0); // Prevent block damage
                fb.setVelocity(new Vector(ThreadLocalRandom.current().nextDouble(-maxOfCenter, maxOfCenter), -1.5, ThreadLocalRandom.current().nextDouble(-maxOfCenter, maxOfCenter)));
                fireballs.add(fb);
            }
        }.runTaskTimer(Main.get(), taskTick, taskTick);
    }

    @Override
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        // Remove all fireballs
        for (Fireball fb : fireballs) {
            if (fb != null && !fb.isDead()) {
                fb.remove();
            }
        }
        fireballs.clear();
    }

    @Override
    public WinEffect clone() {
        return new WinEffectMeteors();
    }
}