package com.undeadlydev.UWinEffects.cosmetics;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import com.undeadlydev.UWinEffects.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class WinEffectFireworks implements WinEffect, Cloneable {

    private BukkitTask task;

    public WinEffectFireworks() {
        this.task = null;
    }

    @Override
    public void start(Player p) {
        task = new BukkitRunnable() {
            final String name = p.getWorld().getName();

            @Override
            public void run() {
                if (p == null || !p.isOnline() || !name.equals(p.getWorld().getName())) {
                    Main.get().getCos().winEffectsTask.remove(p.getUniqueId()).stop();
                    return;
                }
                Utils.firework(p.getLocation());
            }
        }.runTaskTimer(Main.get(), 0, 6);
    }

    @Override
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public WinEffect clone() {
        return new WinEffectFireworks();
    }

	@Override
	public void loadCustoms(Main plugin, String path) {}
}