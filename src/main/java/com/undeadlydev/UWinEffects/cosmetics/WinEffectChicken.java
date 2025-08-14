package com.undeadlydev.UWinEffects.cosmetics;

import com.cryptomorin.xseries.XSound;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import org.bukkit.Location;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class  WinEffectChicken implements WinEffect {

    private final ArrayList<Chicken> chickens = new ArrayList<>();
    private BukkitTask task;

    @Override
    public void start(Player p) {
        Main plugin = Main.get();
        task = new BukkitRunnable() {
            final String name = p.getWorld().getName();

            @Override
            public void run() {
                if (p == null || !p.isOnline() || !name.equals(p.getWorld().getName())) {
                    stop();
                    return;
                }
                Chicken chicken = spawnChicken(p.getLocation(), random(-0.5, 0.5), random(-0.5, 0.5));

                chicken.getLocation().getWorld().playSound(chicken.getLocation(), XSound.ENTITY_CHICKEN_EGG.parseSound(), 1.0f, 1.0f);
                chickens.add(chicken);
                for (Chicken c : new ArrayList<>(chickens)) {
                    if (c.getTicksLived() > 30) {
                        c.remove();
                        chickens.remove(c);
                    }
                }
            }
        }.runTaskTimer(plugin, 5, 5);
    }

    @Override
    public void stop() {
        chickens.clear();
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    public WinEffect clone() {
        return new WinEffectChicken();
    }

    protected double random(double d, double d2) {
        return d + ThreadLocalRandom.current().nextDouble() * (d2 - d);
    }

    private Chicken spawnChicken(Location location, double d, double d3) {
        Chicken chicken = location.getWorld().spawn(location, Chicken.class);
        chicken.setVelocity(new Vector(d, 1.5, d3));
        return chicken;
    }

	@Override
	public void loadCustoms(Main plugin, String path) {}

}