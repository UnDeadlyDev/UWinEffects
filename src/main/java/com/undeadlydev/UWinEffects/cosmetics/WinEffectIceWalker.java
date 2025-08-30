package com.undeadlydev.UWinEffects.cosmetics;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import com.undeadlydev.UWinEffects.managers.CustomSound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WinEffectIceWalker implements WinEffect, Cloneable {

    private BukkitTask task;
    private final Map<Location, BlockData> originalBlocks = new HashMap<>(); // Track original block states

    @Override
    public void start(Player p) {
        World world = p.getWorld();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!p.isOnline() || !p.getWorld().getName().equals(world.getName())) {
                    stop();
                    Main.get().getCos().winEffectsTask.remove(p.getUniqueId()).stop();
                    return;
                }
                for (Block b : getNearbyBlocks(p.getLocation())) {
                    Location loc = b.getLocation();
                    if (!originalBlocks.containsKey(loc)) {
                        originalBlocks.put(loc, b.getBlockData());
                    }
                    b.setType(Material.ICE);
                }
            }
        }.runTaskTimer(Main.get(), 0, 5);
        CustomSound.WINEFFECTS_ICEWALKER.reproduce(p);
    }

    @Override
    public void stop() {
        if (task != null) {
            task.cancel();
        }
        // Revert all changed blocks to their original state
        for (Map.Entry<Location, BlockData> entry : originalBlocks.entrySet()) {
            Block block = entry.getKey().getBlock();
            block.setBlockData(entry.getValue());
        }
        originalBlocks.clear();
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