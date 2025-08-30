package com.undeadlydev.UWinEffects.cosmetics;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class WinEffectAnvilLand implements WinEffect, Cloneable {

    private static boolean loaded = false;
    private static int maxOfPlayerNegative, maxOfPlayerPositive, firstUp, maxRandomUp, taskTick;
    private BukkitTask task;
    private final ArrayList<FallingBlock> fallingBlocks = new ArrayList<>();
    private final Map<Location, BlockData> originalBlocks = new HashMap<>();

    public WinEffectAnvilLand() {
        this.task = null;
    }

    @Override
    public void loadCustoms(Main plugin, String path) {
        if (!loaded) {
            maxOfPlayerNegative = plugin.getWineffect().getIntOrDefault(path + ".maxOfPlayerNegative", 5);
            maxOfPlayerPositive = plugin.getWineffect().getIntOrDefault(path + ".maxOfPlayerPositive", 5);
            firstUp = plugin.getWineffect().getIntOrDefault(path + ".firstUp", 10);
            maxRandomUp = plugin.getWineffect().getIntOrDefault(path + ".maxRandomUp", 20);
            taskTick = plugin.getWineffect().getIntOrDefault(path + ".taskTick", 5);
            loaded = true;
        }
    }

    @Override
    public void start(Player p) {
        World world = p.getWorld();
        task = new BukkitRunnable() {
            public void run() {
                if (p == null || !p.isOnline() || !world.getName().equals(p.getWorld().getName())) {
                    Main.get().getCos().winEffectsTask.remove(p.getUniqueId()).stop();
                    return;
                }
                byte blockData = 0x0;
                Location loc = p.getLocation().clone();
                loc.add(ThreadLocalRandom.current().nextDouble(-maxOfPlayerNegative, maxOfPlayerPositive),
                        ThreadLocalRandom.current().nextDouble(firstUp, maxRandomUp),
                        ThreadLocalRandom.current().nextDouble(-maxOfPlayerNegative, maxOfPlayerPositive));
                // Store the block state at the landing location
                Location landingLoc = loc.clone();
                landingLoc.setY(world.getHighestBlockYAt(loc) + 1); // Approximate landing spot
                if (!originalBlocks.containsKey(landingLoc)) {
                    originalBlocks.put(landingLoc, world.getBlockAt(landingLoc).getBlockData());
                }
                FallingBlock fb = world.spawnFallingBlock(loc, Material.ANVIL, blockData);
                fb.setDropItem(false); // Prevent item drops
                fallingBlocks.add(fb);
            }
        }.runTaskTimer(Main.get(), taskTick, taskTick);
    }

    @Override
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        // Remove all falling blocks
        for (FallingBlock fb : fallingBlocks) {
            if (fb != null && !fb.isDead()) {
                fb.remove();
            }
        }
        fallingBlocks.clear();
        // Revert any placed anvil blocks
        for (Map.Entry<Location, BlockData> entry : originalBlocks.entrySet()) {
            Location loc = entry.getKey();
            BlockData originalData = entry.getValue();
            if (loc.getBlock().getType() == Material.ANVIL) {
                loc.getBlock().setBlockData(originalData);
            }
        }
        originalBlocks.clear();
    }

    @Override
    public WinEffect clone() {
        return new WinEffectAnvilLand();
    }
}