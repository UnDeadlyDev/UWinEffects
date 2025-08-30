package com.undeadlydev.UWinEffects.cosmetics;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import com.undeadlydev.UWinEffects.managers.CustomSound;
import com.undeadlydev.UWinEffects.utils.Utils;
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
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class WinEffectRainbowBeacon implements WinEffect, Cloneable {

    private static boolean loaded = false;
    private static int taskTick;
    private static double radius;
    private static int pillarHeight;
    private final Map<Location, BlockData> originalBlocks = new HashMap<>();
    private BukkitTask task;

    private final Material[] colors = new Material[]{
            Material.RED_STAINED_GLASS,
            Material.ORANGE_STAINED_GLASS,
            Material.YELLOW_STAINED_GLASS,
            Material.LIME_STAINED_GLASS,
            Material.CYAN_STAINED_GLASS,
            Material.BLUE_STAINED_GLASS,
            Material.PURPLE_STAINED_GLASS
    };

    @Override
    public void loadCustoms(Main plugin, String path) {
        if (!loaded) {
            taskTick = plugin.getWineffect().getIntOrDefault(path + ".taskTick", 5);
            radius = plugin.getWineffect().getDoubleOrDefault(path + ".radius", 2.0);
            pillarHeight = plugin.getWineffect().getIntOrDefault(path + ".pillarHeight", 5);
            loaded = true;
        }
    }

    @Override
    public void start(Player p) {
        World world = p.getWorld();
        task = new BukkitRunnable() {
            double angle = 0;
            final double angleIncrement = Math.PI / 12; // 15 degrees per tick
            final String worldName = world.getName();
            int colorIndex = 0;

            @Override
            public void run() {
                if (p == null || !p.isOnline() || !worldName.equals(p.getWorld().getName())) {
                    stop();
                    return;
                }
                angle += angleIncrement;
                colorIndex = (colorIndex + 1) % colors.length; // Cycle through colors
                Location playerLoc = p.getLocation().clone();

                // Calculate pillar position
                Location pillarLoc = playerLoc.clone().add(
                        radius * Math.cos(angle),
                        0,
                        radius * Math.sin(angle)
                );
                pillarLoc.setY(world.getHighestBlockYAt(pillarLoc) + 1);

                // Store original blocks and place colored glass pillar
                for (int y = 0; y < pillarHeight; y++) {
                    Location blockLoc = pillarLoc.clone().add(0, y, 0);
                    Block block = blockLoc.getBlock();
                    if (!originalBlocks.containsKey(blockLoc)) {
                        originalBlocks.put(blockLoc, block.getBlockData());
                    }
                    block.setType(colors[colorIndex]);
                }

                // Play sound and particle effect
                world.playSound(playerLoc, CustomSound.WINEFFECTS_RAINBOWBEACON.getSound(), 1.0f, 1.0f);
                Utils.broadcastParticle(pillarLoc.clone().add(0, pillarHeight, 0), 0, 0, 0, 1, "VILLAGER_HAPPY", 10, 10);

                // Remove old pillars (older than 20 ticks)
                for (Location loc : new ArrayList<>(originalBlocks.keySet())) {
                    Block block = loc.getBlock();
                    if (block.getType() != Material.AIR && System.currentTimeMillis() - block.getData() > 1000) { // Approx 20 ticks
                        block.setBlockData(originalBlocks.get(loc));
                        originalBlocks.remove(loc);
                    }
                }
            }
        }.runTaskTimer(Main.get(), 0, taskTick);
    }

    @Override
    public void stop() {
        // Revert all modified blocks
        for (Map.Entry<Location, BlockData> entry : originalBlocks.entrySet()) {
            Location loc = entry.getKey();
            BlockData originalData = entry.getValue();
            loc.getBlock().setBlockData(originalData);
        }
        originalBlocks.clear();
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public WinEffect clone() {
        return new WinEffectRainbowBeacon();
    }
}