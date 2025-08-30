package com.undeadlydev.UWinEffects.cosmetics;

import com.cryptomorin.xseries.XMaterial;

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

public class WinEffectVulcanWool implements WinEffect, Cloneable {

    private Collection<FallingBlock> fires = new ArrayList<>();
    private BukkitTask task;

    private Material[] wools = new Material[]{XMaterial.WHITE_WOOL.parseMaterial(), XMaterial.LIGHT_GRAY_WOOL.parseMaterial(), XMaterial.GRAY_WOOL.parseMaterial(), XMaterial.BLACK_WOOL.parseMaterial(), XMaterial.BROWN_WOOL.parseMaterial(), XMaterial.RED_WOOL.parseMaterial(), XMaterial.ORANGE_WOOL.parseMaterial(), XMaterial.YELLOW_WOOL.parseMaterial(), XMaterial.LIME_WOOL.parseMaterial(), XMaterial.GREEN_WOOL.parseMaterial(), XMaterial.CYAN_WOOL.parseMaterial(), XMaterial.LIGHT_BLUE_WOOL.parseMaterial(), XMaterial.BLUE_WOOL.parseMaterial(), XMaterial.PURPLE_WOOL.parseMaterial(), XMaterial.MAGENTA_WOOL.parseMaterial(), XMaterial.PINK_WOOL.parseMaterial()};

    @Override
    public void start(Player p) {

        String name = p.getLocation().getWorld().getName();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (p == null || !p.isOnline() || !name.equals(p.getWorld().getName())) {
                    stop();
                    Main.get().getCos().winEffectsTask.remove(p.getUniqueId()).stop();
                    return;
                }
                CustomSound.WINEFFECTS_VULCANWOOL.reproduce(p);
                FallingBlock fallingBlock = spawnWool(p.getLocation(), random(-0.5, 0.5), random(-0.5, 0.5));
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
        return new WinEffectVulcanWool();
    }

    protected double random(double d, double d2) {
        return d + ThreadLocalRandom.current().nextDouble() * (d2 - d);
    }

    private FallingBlock spawnWool(Location location, double d, double d3) {
        FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location, this.wools[ThreadLocalRandom.current().nextInt(this.wools.length)], (byte) ThreadLocalRandom.current().nextInt(15));
        fallingBlock.setVelocity(new Vector(d, 0.75, d3));
        return fallingBlock;
    }

    @Override
    public void loadCustoms(Main plugin, String path) {}

}