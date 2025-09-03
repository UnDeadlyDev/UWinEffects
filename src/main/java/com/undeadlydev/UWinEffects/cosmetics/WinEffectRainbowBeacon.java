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
import com.cryptomorin.xseries.XMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WinEffectRainbowBeacon implements WinEffect, Cloneable {

    private static int taskTick;
    private static double radius;
    private static int pillarHeight;
    private final Map<Location, BlockData> originalBlocks = new HashMap<>();
    private BukkitTask task;

    private final Material[] colors = new Material[]{
            XMaterial.RED_STAINED_GLASS.parseMaterial(),
            XMaterial.ORANGE_STAINED_GLASS.parseMaterial(),
            XMaterial.YELLOW_STAINED_GLASS.parseMaterial(),
            XMaterial.LIME_STAINED_GLASS.parseMaterial(),
            XMaterial.CYAN_STAINED_GLASS.parseMaterial(),
            XMaterial.BLUE_STAINED_GLASS.parseMaterial(),
            XMaterial.PURPLE_STAINED_GLASS.parseMaterial()
    };

    @Override
    public void loadCustoms(Main plugin, String path) {
        taskTick = plugin.getWineffect().getIntOrDefault(path + ".taskTick", 5);
        radius = plugin.getWineffect().getDoubleOrDefault(path + ".radius", 2.0);
        pillarHeight = plugin.getWineffect().getIntOrDefault(path + ".pillarHeight", 5);
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
                    Main.get().getCos().winEffectsTask.remove(p.getUniqueId()).stop();
                    return;
                }

                // Actualizar y restaurar bloques antiguos
                Location playerLoc = p.getLocation().clone();
                Iterator<Map.Entry<Location, BlockData>> it = originalBlocks.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Location, BlockData> entry = it.next();
                    Location loc = entry.getKey();
                    Block block = loc.getBlock();
                    block.setBlockData(entry.getValue());
                    it.remove();
                }

                angle += angleIncrement;
                colorIndex = (colorIndex + 1) % colors.length;

                Location pillarLoc = playerLoc.clone().add(
                        radius * Math.cos(angle),
                        0,
                        radius * Math.sin(angle)
                );
                pillarLoc.setY(playerLoc.getY());

                for (int y = 0; y < pillarHeight; y++) {
                    Location blockLoc = pillarLoc.clone().add(0, y, 0);
                    Block block = blockLoc.getBlock();
                    originalBlocks.put(blockLoc, block.getBlockData()); // Guardar el estado original
                    block.setType(colors[colorIndex]);
                }
                CustomSound.WINEFFECTS_RAINBOWBEACON.reproduce(p);
                Utils.broadcastParticle(pillarLoc.clone().add(0, pillarHeight + 0.5, 0), 0, 0, 0, 1, "HAPPY_VILLAGER", 20, 10);
            }
        }.runTaskTimer(Main.get(), 0, taskTick);
    }

    @Override
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        for (Map.Entry<Location, BlockData> entry : originalBlocks.entrySet()) {
            Location loc = entry.getKey();
            BlockData originalData = entry.getValue();
            Block block = loc.getBlock();
            block.setBlockData(originalData); // Siempre restaura el bloque
        }
        originalBlocks.clear();
    }

    @Override
    public WinEffect clone() {
        return new WinEffectRainbowBeacon();
    }
}