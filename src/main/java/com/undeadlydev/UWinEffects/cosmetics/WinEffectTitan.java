package com.undeadlydev.UWinEffects.cosmetics;

import com.cryptomorin.xseries.XAttribute;
import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import org.bukkit.World;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class WinEffectTitan implements WinEffect {

    private BukkitTask task;
    private  Player player;

    @Override
    public void start(Player p) {
        World world = p.getWorld();
        player = p;
        AttributeInstance scaleAttribute = player.getAttribute(XAttribute.SCALE.get());
        task = new BukkitRunnable() {
            public void run() {
                if (p == null || !p.isOnline() || !world.getName().equals(p.getWorld().getName())) {
                    scaleAttribute.setBaseValue(1.0);
                    stop();
                    return;
                }
                scaleAttribute.setBaseValue(35.0);
            }
        }.runTaskTimer(Main.get(), 0, 10L);
    }

    @Override
    public void stop() {
        AttributeInstance scaleAttribute = player.getAttribute(XAttribute.SCALE.get());
        if (task != null) {
            scaleAttribute.setBaseValue(1.0);
            task.cancel();
        }
    }

    @Override
    public WinEffect clone() {
        return new WinEffectTitan();
    }

    @Override
    public void loadCustoms(Main plugin, String path) {}
}
