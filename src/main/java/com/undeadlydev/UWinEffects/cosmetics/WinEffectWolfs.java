package com.undeadlydev.UWinEffects.cosmetics;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WinEffectWolfs implements WinEffect, Cloneable {

    private static boolean loaded = false;
    private static int taskTick;
    private BukkitTask task;
    private final List<Wolf> wolves = new ArrayList<>(); // Track spawned wolves

    @Override
    public void loadCustoms(Main plugin, String path) {
        if (!loaded) {
            loaded = true;
            this.task = null;
            taskTick = plugin.getWineffect().getIntOrDefault(path + ".taskTick", 5);
        }
    }

    @Override
    public void start(Player p) {
        World world = p.getWorld();
        task = new BukkitRunnable() {
            public void run() {
                if (p == null || !p.isOnline() || !world.getName().equals(p.getWorld().getName())) {
                    stop();
                    Main.get().getCos().winEffectsTask.remove(p.getUniqueId()).stop();
                    return;
                }
                for (int i = 0; i < 2; i++) {
                    Location loc = p.getLocation();
                    Wolf wolf = world.spawn(loc.add(ThreadLocalRandom.current().nextDouble(-3, 3), ThreadLocalRandom.current().nextDouble(1, 2), ThreadLocalRandom.current().nextDouble(-3, 3)), Wolf.class);
                    wolf.setSitting(ThreadLocalRandom.current().nextBoolean());
                    wolf.setNoDamageTicks(Integer.MAX_VALUE);
                    wolves.add(wolf);
                }
            }
        }.runTaskTimer(Main.get(), taskTick, taskTick);
    }

    @Override
    public void stop() {
        if (task != null) {
            task.cancel();
        }
        for (Wolf wolf : wolves) {
            if (wolf != null && !wolf.isDead()) {
                wolf.remove();
            }
        }
        wolves.clear();
    }

    @Override
    public WinEffect clone() {
        return new WinEffectWolfs();
    }

}