package com.undeadlydev.UWinEffects.cosmetics;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WinEffectPigLand implements WinEffect, Cloneable {

    private static int maxOfPlayerNegative, maxOfPlayerPositive, firstUp, maxRandomUp, taskTick;
    private BukkitTask task;
    private final List<Pig> pigs = new ArrayList<>();

    public WinEffectPigLand() {
        this.task = null;
    }

    @Override
    public void loadCustoms(Main plugin, String path) {
        maxOfPlayerNegative = plugin.getWineffect().getIntOrDefault(path + ".maxOfPlayerNegative", 5);
        maxOfPlayerPositive = plugin.getWineffect().getIntOrDefault(path + ".maxOfPlayerPositive", 5);
        firstUp = plugin.getWineffect().getIntOrDefault(path + ".firstUp", 5);
        maxRandomUp = plugin.getWineffect().getIntOrDefault(path + ".maxRandomUp", 10);
        taskTick = plugin.getWineffect().getIntOrDefault(path + ".taskTick", 5);
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
                for (int i = 0; i < 4; i++) {
                    Location loc = p.getLocation();
                    Pig p1 = world.spawn(loc.add(ThreadLocalRandom.current().nextDouble(-maxOfPlayerNegative, maxOfPlayerPositive), ThreadLocalRandom.current().nextDouble(firstUp, maxRandomUp), ThreadLocalRandom.current().nextDouble(-maxOfPlayerNegative, maxOfPlayerPositive)), Pig.class);
                    p1.setNoDamageTicks(Integer.MAX_VALUE);
                    p1.setFallDistance(Integer.MAX_VALUE);
                    pigs.add(p1);
                }
            }
        }.runTaskTimer(Main.get(), taskTick, taskTick);
    }

    @Override
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        for (Pig pig : pigs) {
            if (pig != null && !pig.isDead()) {
                pig.remove();
            }
        }
        pigs.clear();
    }

    @Override
    public WinEffect clone() {
        return new WinEffectPigLand();
    }

}