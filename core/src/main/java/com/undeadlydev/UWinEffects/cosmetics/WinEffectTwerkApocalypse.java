package com.undeadlydev.UWinEffects.cosmetics;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.CustomNPC;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WinEffectTwerkApocalypse implements WinEffect, Cloneable {
    private final List<CustomNPC> npcs = new ArrayList<>();
    private BukkitTask task;

    @Override
    public void start(Player p) {
        Main plugin = Main.get();
        World world = p.getWorld();

        for (int i = 0; i < 20; i++) {
            Location spawnLoc = getSafeSpawnLocation(p.getLocation(), -10, 10);
            try {
                spawnLoc.setY(spawnLoc.getY() + 1);
                CustomNPC npc = plugin.getCustomNPC().createNPC(spawnLoc, "TwerkNPC_" + i, p);
                npc.spawn(p); // Spawn for the player
                npcs.add(npc);
                Main.get().sendDebugMessage("§aSpawned NPC #" + i + " at " + spawnLoc);
            } catch (Exception e) {
                Main.get().sendDebugMessage("§cFailed to create NPC #" + i + ": " + e.getMessage());
            }
        }

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (p == null || !p.isOnline() || !world.getName().equals(p.getWorld().getName())) {
                    Main.get().getCos().winEffectsTask.remove(p.getUniqueId()).stop();
                    return;
                }
                for (CustomNPC npc : npcs) {
                    try {
                        double randomChance = ThreadLocalRandom.current().nextDouble();
                        if (randomChance < 0.5) { // 50% chance to toggle pose
                            boolean currentSneaking = npc.isSneaking();
                            npc.setPose(!currentSneaking);
                        }
                    } catch (Exception e) {
                        p.sendMessage("§cError updating NPC pose: " + e.getMessage());
                    }
                }
            }
        }.runTaskTimer(Main.get(), 0, 10);
    }

    @Override
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        for (CustomNPC npc : new ArrayList<>(npcs)) {
            try {
                if (npc != null) {
                    npc.destroy();
                }
            } catch (Exception e) {
                System.out.println("Error deleting NPC: " + e.getMessage());
            }
        }
        npcs.clear();
    }

    private double random(double min, double max) {
        return min + ThreadLocalRandom.current().nextDouble() * (max - min);
    }

    private Location getSafeSpawnLocation(Location base, double minOffset, double maxOffset) {
        World world = base.getWorld();
        if (world == null) return null;

        for (int attempt = 0; attempt < 10; attempt++) {
            double x = base.getX() + random(minOffset, maxOffset);
            double z = base.getZ() + random(minOffset, maxOffset);
            int baseY = base.getBlockY();

            for (int y = baseY + 10; y >= baseY - 10; y--) {
                Location loc = new Location(world, x, y, z);
                if (loc.getChunk().isLoaded() && isSafeLocation(loc)) {
                    return loc;
                }
            }
        }
        return null;
    }

    private boolean isSafeLocation(Location loc) {
        Block feet = loc.getBlock();
        Block head = loc.clone().add(0, 1, 0).getBlock();
        return !feet.isEmpty() && !feet.isLiquid() && head.isEmpty() && !head.isLiquid();
    }

    @Override
    public WinEffect clone() {
        return new WinEffectTwerkApocalypse();
    }

    @Override
    public void loadCustoms(Main plugin, String path) {}
}