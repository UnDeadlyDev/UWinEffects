package com.undeadlydev.UWinEffects.cosmetics;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import com.undeadlydev.UWinEffects.enums.CustomSound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WinEffectWolfs implements WinEffect, Cloneable {

    private static boolean loaded = false;
    private final List<Wolf> wolves = new ArrayList<>(); // Track spawned wolves

    @Override
    public void loadCustoms(Main plugin, String path) {
        if (!loaded) {
            loaded = true;
        }
    }

    @Override
    public void start(Player p) {
        World world = p.getWorld();
        CustomSound.WINEFFECTS_WOLFS.reproduce(p);
        for (int i = 0; i < 10; i++) {
            Location loc = p.getLocation();
            Wolf wolf = world.spawn(loc.add(ThreadLocalRandom.current().nextDouble(-3, 3), ThreadLocalRandom.current().nextDouble(1, 2), ThreadLocalRandom.current().nextDouble(-3, 3)), Wolf.class);
            wolf.setOwner(p);
            wolf.setTamed(true);
            wolf.setSitting(ThreadLocalRandom.current().nextBoolean());
            wolf.setNoDamageTicks(Integer.MAX_VALUE);
            wolves.add(wolf);
        }
    }

    @Override
    public void stop() {
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