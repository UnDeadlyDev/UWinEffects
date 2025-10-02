package com.undeadlydev.UWinEffects.cosmetics;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import com.undeadlydev.UWinEffects.enums.CustomSound;
import com.undeadlydev.UWinEffects.utils.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class WinEffectStarShower implements WinEffect, Cloneable {

    private static int taskTick;
    private static double radius;
    private final ArrayList<ShulkerBullet> stars = new ArrayList<>();
    private BukkitTask task;

    @Override
    public void loadCustoms(Main plugin, String path) {
        taskTick = plugin.getWineffect().getIntOrDefault(path + ".taskTick", 4);
        radius = plugin.getWineffect().getDoubleOrDefault(path + ".radius", 3.0);
    }

    @Override
    public void start(Player p) {
        World world = p.getWorld();
        task = new BukkitRunnable() {
            double angle = 0;
            final double angleIncrement = Math.PI / 18; // 10 degrees per tick for smooth rotation
            final String worldName = world.getName();

            @Override
            public void run() {
                if (p == null || !p.isOnline() || !worldName.equals(p.getWorld().getName())) {
                    Main.get().getCos().winEffectsTask.remove(p.getUniqueId()).stop();
                    return;
                }
                angle += angleIncrement;
                Location playerLoc = p.getLocation().clone().add(0, 1, 0); // Center at player height

                // Spawn a star (ShulkerBullet) with a spiral trajectory
                Location spawnLoc = playerLoc.clone().add(
                        radius * Math.cos(angle),
                        ThreadLocalRandom.current().nextDouble(2, 4),
                        radius * Math.sin(angle)
                );
                ShulkerBullet star = world.spawn(spawnLoc, ShulkerBullet.class);
                star.setGravity(false);
                star.setVelocity(new Vector(
                        ThreadLocalRandom.current().nextDouble(-0.2, 0.2),
                        -0.3, // Gentle downward drift
                        ThreadLocalRandom.current().nextDouble(-0.2, 0.2)
                ));
                stars.add(star);

                // Play sound and particle effect
                world.playSound(playerLoc, CustomSound.WINEFFECTS_STARSHOWER.getSound(), 1.0f, 1.5f); // High-pitched sparkle sound
                Utils.broadcastParticle(playerLoc, 0, 0, 0, 1, "ENCHANTED_HIT", 10, 10);

                // Cleanup stars older than 40 ticks
                for (ShulkerBullet s : new ArrayList<>(stars)) {
                    if (s.getTicksLived() > 40) {
                        s.remove();
                        stars.remove(s);
                        Utils.broadcastParticle(s.getLocation(), 0, 0, 0, 1, "FIREWORK", 5, 10);
                    }
                }
            }
        }.runTaskTimer(Main.get(), 0, taskTick);
    }

    @Override
    public void stop() {
        // Remove all stars
        for (ShulkerBullet star : stars) {
            if (star != null && !star.isDead()) {
                star.remove();
                Utils.broadcastParticle(star.getLocation(), 0, 0, 0, 1, "FIREWORK", 5, 10);
            }
        }
        stars.clear();
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public WinEffect clone() {
        return new WinEffectStarShower();
    }
}