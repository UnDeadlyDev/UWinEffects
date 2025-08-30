package com.undeadlydev.UWinEffects.cosmetics;

import com.cryptomorin.xseries.XMaterial;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import com.undeadlydev.UWinEffects.managers.CustomSound;
import com.undeadlydev.UWinEffects.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class WinEffectNotes implements WinEffect {

    private ArrayList<Item> items = new ArrayList<>();
    private BukkitTask task;
    private Material[] discs = new Material[]{
            XMaterial.MUSIC_DISC_13.parseMaterial(),
            XMaterial.MUSIC_DISC_CAT.parseMaterial(),
            XMaterial.MUSIC_DISC_BLOCKS.parseMaterial(),
            XMaterial.MUSIC_DISC_CHIRP.parseMaterial(),
            XMaterial.MUSIC_DISC_FAR.parseMaterial(),
            XMaterial.MUSIC_DISC_MALL.parseMaterial(),
            XMaterial.MUSIC_DISC_MELLOHI.parseMaterial(),
            XMaterial.MUSIC_DISC_STAL.parseMaterial(),
            XMaterial.MUSIC_DISC_STRAD.parseMaterial(),
            XMaterial.MUSIC_DISC_WARD.parseMaterial(),
            XMaterial.MUSIC_DISC_11.parseMaterial(),
            XMaterial.MUSIC_DISC_WAIT.parseMaterial()
    };

    @Override
    public void start(Player p) {
        task = new BukkitRunnable() {
            String name = p.getWorld().getName();

            @Override
            public void run() {
                if (p == null || !p.isOnline() || !name.equals(p.getWorld().getName())) {
                    stop();
                    Main.get().getCos().winEffectsTask.remove(p.getUniqueId()).stop();
                    return;
                }
                Item item = spawnDisc(p.getLocation(), random(-0.25, 0.25), random(-0.25, 0.25));
                CustomSound.WINEFFECTS_NOTES.reproduce(p);
                Utils.broadcastParticle(p.getLocation(), ThreadLocalRandom.current().nextInt(0, 24), 0, 0, 1, "NOTE", 5, 10);
                items.add(item);
                for (Item c : new ArrayList<>(items)) {
                    if (c.getTicksLived() > 30) {
                        c.remove();
                        Utils.broadcastParticle(c.getLocation(), ThreadLocalRandom.current().nextInt(0, 24), 0, 0, 1, "NOTE", 5, 10);
                        items.remove(c);
                    }
                }
            }
        }.runTaskTimer(Main.get(), 0, 6);
    }

    @Override
    public void stop() {
        // Remove all tracked items
        for (Item item : items) {
            if (item != null && !item.isDead()) {
                item.remove();
            }
        }
        items.clear();
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public WinEffect clone() {
        return new WinEffectNotes();
    }

    protected double random(double d, double d2) {
        return d + ThreadLocalRandom.current().nextDouble() * (d2 - d);
    }

    private Item spawnDisc(Location location, double d, double d3) {
        ItemStack itemStack = new ItemStack(this.discs[ThreadLocalRandom.current().nextInt(this.discs.length)]);
        Item item = location.getWorld().dropItem(location, itemStack);
        item.setPickupDelay(Integer.MAX_VALUE);
        item.setVelocity(new Vector(d, 0.8, d3));
        return item;
    }

    @Override
    public void loadCustoms(Main plugin, String path) {}
}