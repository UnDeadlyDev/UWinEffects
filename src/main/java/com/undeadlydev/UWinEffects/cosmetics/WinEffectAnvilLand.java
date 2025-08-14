package com.undeadlydev.UWinEffects.cosmetics;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ThreadLocalRandom;

public class WinEffectAnvilLand implements WinEffect, Cloneable {

    private static boolean loaded = false;
    private static int maxOfPlayerNegative, maxOfPlayerPositive, firstUp, maxRandomUp, taskTick;
    private BukkitTask task;

    public WinEffectAnvilLand() {
        this.task = null;
    }

    @Override
    public void loadCustoms(Main plugin, String path) {
        if (!loaded) {
        	maxOfPlayerNegative = plugin.getWineffect().getIntOrDefault(path + ".maxOfPlayerNegative", 5);
        	maxOfPlayerPositive = plugin.getWineffect().getIntOrDefault(path + ".maxOfPlayerPositive", 5);
            firstUp = plugin.getWineffect().getIntOrDefault(path + ".firstUp", 10);
            maxRandomUp = plugin.getWineffect().getIntOrDefault(path + ".maxRandomUp", 20);
            taskTick = plugin.getWineffect().getIntOrDefault(path + ".taskTick", 5);
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
                byte blockData = 0x0;
                Location loc = p.getLocation();
                world.spawnFallingBlock(loc.add(ThreadLocalRandom.current().nextDouble(-maxOfPlayerNegative, maxOfPlayerPositive), ThreadLocalRandom.current().nextDouble(firstUp, maxRandomUp), ThreadLocalRandom.current().nextDouble(-maxOfPlayerNegative, maxOfPlayerPositive)), Material.ANVIL, blockData);
                
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
        return new WinEffectAnvilLand();
    }

}