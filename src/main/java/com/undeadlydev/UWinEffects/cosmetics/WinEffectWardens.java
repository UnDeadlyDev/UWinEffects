package com.undeadlydev.UWinEffects.cosmetics;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class WinEffectWardens implements WinEffect, Cloneable {

    private final ArrayList<Warden> wardens = new ArrayList<>();
    private BukkitTask task;

    @Override
    public void start(Player p) {
        World world = p.getWorld();
        Location l1 = getCicle(p.getLocation(), 0, 2);
        Location l2 = getCicle(p.getLocation(), 10, 2);
        Location l3 = getCicle(p.getLocation(), 15, 2);
        Warden g1 = apply(l1.getWorld().spawn(l1, Warden.class));
        Warden g2 = apply(l2.getWorld().spawn(l2, Warden.class));
        Warden g3 = apply(l3.getWorld().spawn(l3, Warden.class));
        wardens.add(g1);
        wardens.add(g2);
        wardens.add(g3);
        task = new BukkitRunnable() {
            final double add = Math.PI / 36;
            double angle = 0;

            @Override
            public void run() {
                if (p == null || !p.isOnline() || !world.getName().equals(p.getWorld().getName())) {
                    stop();
                    Main.get().getCos().winEffectsTask.remove(p.getUniqueId()).stop();
                    return;
                }
                angle += add;
                for (int i = 0; i < wardens.size(); i++) {
                    Warden w = wardens.get(i);
                    double currentAngle = angle + (i * (Math.PI * 2 / wardens.size())); // Distribuir alrededor del jugador
                    Location now = getCicle(p.getLocation(), currentAngle, 2); // Radio de 2 bloques alrededor del jugador
                    w.teleport(now);
                }
            }
        }.runTaskTimer(Main.get(), 0, 2);
    }

    @Override
    public void stop() {
        for (Warden w : wardens) {
            if (w == null || w.isDead()) continue;
            w.remove();
        }
        wardens.clear();
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    public WinEffect clone() {
        return new WinEffectWardens();
    }

    public Warden apply(Warden w) {
        w.setNoDamageTicks(Integer.MAX_VALUE);
        w.setAI(false); // Desactivar IA para que no sigan su comportamiento normal
        return w;
    }

    public Location getCicle(Location loc, double angle, double radius) {
        double x = radius * Math.cos(angle);
        double z = radius * Math.sin(angle);
        return loc.clone().add(x, 0, z);
    }

    @Override
    public void loadCustoms(Main plugin, String path) {}
}
