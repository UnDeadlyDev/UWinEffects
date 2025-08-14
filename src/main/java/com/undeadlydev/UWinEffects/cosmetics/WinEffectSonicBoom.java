package com.undeadlydev.UWinEffects.cosmetics;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class WinEffectSonicBoom implements WinEffect, Cloneable {

    private static boolean loaded = false;
    private static int taskTick;
    private static int durationTicks;
    private static double forwardDistance;
    private static double particleDensity;
    private static double damageRadius;
    private static double damageAmount;

    private BukkitTask task;
    private int ticksElapsed;

    public WinEffectSonicBoom() {
        this.task = null;
        this.ticksElapsed = 0;
    }

    @Override
    public void loadCustoms(Main plugin, String path) {
        if (!loaded) {
            taskTick = plugin.getWineffect().getIntOrDefault(path + ".taskTick", 1);
            durationTicks = plugin.getWineffect().getIntOrDefault(path + ".durationTicks", 30);
            forwardDistance = plugin.getWineffect().getDoubleOrDefault(path + ".forwardDistance", 10.0);
            particleDensity = plugin.getWineffect().getDoubleOrDefault(path + ".particleDensity", 0.5);
            damageRadius = plugin.getWineffect().getDoubleOrDefault(path + ".damageRadius", 3.0);
            damageAmount = plugin.getWineffect().getDoubleOrDefault(path + ".damageAmount", 6.0);

            loaded = true;
        }
    }

    @Override
    public void start(Player p) {
        Location centerLoc = p.getLocation().clone().subtract(0, 0.5, 0);
        World world = p.getWorld();
        this.ticksElapsed = 0;
        p.playSound(p.getLocation(), XSound.ENTITY_WARDEN_SONIC_CHARGE.get(), 1.0f, 1.0f);

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (p == null || !p.isOnline() || ticksElapsed >= durationTicks || !world.getName().equals(p.getWorld().getName())) {
                    stop();
                    return;
                }
                Location origin = p.getLocation().clone().add(0, 1.0, 0);

                Vector direction = p.getLocation().getDirection().normalize();

                // Simular el rayo de partículas
                for (double i = 0; i < forwardDistance; i += particleDensity) {
                    Location particleLoc = origin.clone().add(direction.clone().multiply(i));
                    p.getWorld().spawnParticle(XParticle.SONIC_BOOM.get(), particleLoc, 1, 0.0, 0.0, 0.0, 0.0f);
                }
                if (ticksElapsed == 0 || ticksElapsed == 5) {
                    p.playSound(p.getLocation(), XSound.ENTITY_WARDEN_SONIC_BOOM.get(), 1.0f, 1.0f);
                    for (org.bukkit.entity.Entity entity : p.getNearbyEntities(damageRadius, damageRadius, damageRadius)) {
                        if (entity instanceof Player && entity != p) {
                            ((Player) entity).damage(damageAmount, p);
                        }
                    }
                }

                ticksElapsed++;
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
        return new WinEffectSonicBoom();
    }
}