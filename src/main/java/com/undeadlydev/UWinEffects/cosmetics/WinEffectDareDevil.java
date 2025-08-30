package com.undeadlydev.UWinEffects.cosmetics;

import com.cryptomorin.xseries.XSound;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import com.undeadlydev.UWinEffects.managers.CustomSound;
import com.undeadlydev.UWinEffects.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class WinEffectDareDevil implements WinEffect {

    private Vehicle horse;

    private BukkitTask task;

	@Override
    public void start(Player p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                horse = Utils.spawnHorse(p.getLocation(), p);
                horse.setPassenger(p);
            }
        }.runTaskLater(Main.get(), 1);
        CustomSound.WINEFFECTS_DAREDEVIL.reproduce(p);
        task = new BukkitRunnable() {
            final String name = p.getWorld().getName();
            @Override
            public void run() {
                if (p == null || !p.isOnline() || !name.equals(p.getWorld().getName())) {
                    Main.get().getCos().winEffectsTask.remove(p.getUniqueId()).stop();
                }
            }
        }.runTaskTimer(Main.get(), 0, 20);
    }

    @Override
    public void stop() {
        if (horse != null) {
            horse.remove();
        }
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    public WinEffect clone() {
        return new WinEffectDareDevil();
    }

	@Override
	public void loadCustoms(Main plugin, String path) {}
}