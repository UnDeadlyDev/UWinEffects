package com.undeadlydev.UWinEffects.cosmetics;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import com.undeadlydev.UWinEffects.managers.CustomSound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public class WinEffectVulcanFire implements WinEffect, Cloneable {

    private Collection<FallingBlock> fires = new ArrayList<>();
    private BukkitTask task;

    @Override
    public void start(Player p) {
        task = new BukkitRunnable() {
            final String name = p.getWorld().getName();

            @Override
            public void run() {
                if (p == null || !p.isOnline() || !name.equals(p.getWorld().getName())) {
                    stop();
                    Main.get().getCos().winEffectsTask.remove(p.getUniqueId()).stop();
                    return;
                }
                CustomSound.WINEFFECTS_VULCANFIRE.reproduce(p);
                FallingBlock fallingBlock = spawnFire(p.getLocation(), random(-0.5, 0.5), random(-0.5, 0.5));
                fallingBlock.setDropItem(false);
                fires.add(fallingBlock);
            }
        }.runTaskTimer(Main.get(), 0, 2);
    }

    @Override
    public void stop() {
        if (task != null) {
            task.cancel();
        }
        for (FallingBlock fb : fires) {
            if (fb == null)
                continue;
            if (!fb.isDead()) {
                fb.remove();
            } else if (fb.isOnGround()) {
                fb.getLocation().getBlock().setType(Material.AIR);
            }
        }
        fires.clear(); // Clear the list after cleanup
    }

    @Override
    public WinEffect clone() {
        return new WinEffectVulcanFire();
    }

    protected double random(double d, double d2) {
        return d + ThreadLocalRandom.current().nextDouble() * (d2 - d);
    }

    private FallingBlock spawnFire(Location location, double d, double d3) {
        FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location, Material.FIRE, (byte) ThreadLocalRandom.current().nextInt(15));
        fallingBlock.setVelocity(new Vector(d, 0.75, d3));
        return fallingBlock;
    }

    @Override
    public void loadCustoms(Main plugin, String path) {}

}