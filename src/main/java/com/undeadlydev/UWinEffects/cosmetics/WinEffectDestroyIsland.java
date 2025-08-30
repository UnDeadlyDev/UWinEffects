package com.undeadlydev.UWinEffects.cosmetics;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WinEffectDestroyIsland implements WinEffect, Cloneable {

    private static boolean loaded = false;
    private static int spawnLaterTick, amountTNT, perFuseAmount;
    private BukkitTask task;
    private final ArrayList<TNTPrimed> tnts = new ArrayList<>();
    private final Map<Location, BlockData> originalBlocks = new HashMap<>();

    @Override
    public void loadCustoms(Main plugin, String path) {
        if (!loaded) {
            spawnLaterTick = plugin.getWineffect().getIntOrDefault(path + ".spawnLaterTick", 20);
            amountTNT = plugin.getWineffect().getIntOrDefault(path + ".amountTNT", 4);
            perFuseAmount = plugin.getWineffect().getIntOrDefault(path + ".perFuseAmount", 15);
            loaded = true;
        }
    }

    @Override
    public void start(Player p) {
        World world = p.getWorld();
        task = new BukkitRunnable() {
            public void run() {
                if (p == null || !p.isOnline() || !world.getName().equals(p.getWorld().getName())) {
                    stop();
                    Main.get().getCos().winEffectsTask.remove(p.getUniqueId()).stop();
                    return;
                }
                explode(p.getLocation());
            }
        }.runTaskTimer(Main.get(), spawnLaterTick, spawnLaterTick);
    }

    private void explode(Location loc) {
        loc.getWorld().strikeLightning(loc);
        // Track blocks in a small radius around the explosion point
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    Location blockLoc = loc.clone().add(x, y, z);
                    Block block = blockLoc.getBlock();
                    if (!originalBlocks.containsKey(blockLoc)) {
                        originalBlocks.put(blockLoc, block.getBlockData());
                    }
                }
            }
        }
        int pa = perFuseAmount;
        for (int i = 0; i < amountTNT; i++) {
            TNTPrimed tnt = loc.getWorld().spawn(loc, TNTPrimed.class);
            tnt.setFuseTicks(pa);
            tnts.add(tnt);
            pa += perFuseAmount;
        }
    }

    @Override
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        // Remove all TNT entities
        for (TNTPrimed tnt : tnts) {
            if (tnt != null && !tnt.isDead()) {
                tnt.remove();
            }
        }
        tnts.clear();
        // Revert affected blocks
        for (Map.Entry<Location, BlockData> entry : originalBlocks.entrySet()) {
            Location loc = entry.getKey();
            BlockData originalData = entry.getValue();
            loc.getBlock().setBlockData(originalData);
        }
        originalBlocks.clear();
    }

    @Override
    public WinEffect clone() {
        return new WinEffectDestroyIsland();
    }
}