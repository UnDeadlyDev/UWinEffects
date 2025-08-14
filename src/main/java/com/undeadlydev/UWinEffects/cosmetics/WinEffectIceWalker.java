package com.undeadlydev.UWinEffects.cosmetics;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class WinEffectIceWalker implements WinEffect, Cloneable {

    private BukkitTask task;

    @Override
    public void start(Player p) {
        World world = p.getWorld();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!p.isOnline() || !p.getWorld().getName().equals(world.getName())) {
                    stop();
                    return;
                }
                for (Block b : getNearbyBlocks(p.getLocation())) {
                    b.setType(Material.ICE);
                }
            }
        }.runTaskTimer(Main.get(), 0, 5);
    }

    @Override
    public void stop() {
        if (task != null) {
            task.cancel();
        }
    }

    private List<Block> getNearbyBlocks(Location location) {
        List<Block> blocks = new ArrayList<>();
        for (int x = location.getBlockX() - 2; x <= location.getBlockX() + 2; x++) {
            for (int y = location.getBlockY() - 2; y <= location.getBlockY() + 2; y++) {
                for (int z = location.getBlockZ() - 2; z <= location.getBlockZ() + 2; z++) {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == Material.AIR || block.getType() == Material.ICE || block.getType() == Material.PACKED_ICE) {
                        continue;
                    }
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    @Override
    public WinEffect clone() {
        return new WinEffectIceWalker();
    }

	@Override
	public void loadCustoms(Main plugin, String path) {}
}