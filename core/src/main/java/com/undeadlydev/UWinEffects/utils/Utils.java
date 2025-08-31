package com.undeadlydev.UWinEffects.utils;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.particles.XParticle;
import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.data.DBPlayer;
import com.undeadlydev.UWinEffects.managers.FileManager;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.text.DecimalFormat;
import java.util.Random;

public class Utils {

    private final static Main plugin = Main.get();

    public final static Random random = new Random();

    public static String toGson(DBPlayer sw) {
        return plugin.getGson().toJson(sw, DBPlayer.class);
    }

    public static DBPlayer fromGson(String data) {
        return plugin.getGson().fromJson(data, DBPlayer.class);
    }

    public static void broadcastParticle(Location location, float offsetX, float offsetY, float offsetZ, int speed, String enumParticle, int amount, double range) {
        if (location.getWorld() == null)
            return;
        location.getWorld().spawnParticle(XParticle.valueOf(enumParticle).get(), location, amount, offsetX, offsetY, offsetZ, speed);
    }

    public static void firework(Location loc) {
        Firework fa = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fam = fa.getFireworkMeta();
        FireworkEffect.Type tipo = FireworkEffect.Type.values()[random.nextInt(4)];
        Color c1 = Color.fromBGR(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        Color c2 = Color.fromBGR(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        FireworkEffect ef = FireworkEffect.builder().withColor(c1).withFade(c2).with(tipo).build();
        fam.addEffect(ef);
        fam.setPower(0);
        fa.setFireworkMeta(fam);
    }

    public static Vehicle spawnHorse(Location loc, Player p) {
        SkeletonHorse horse = loc.getWorld().spawn(loc, SkeletonHorse.class);
        AbstractHorseInventory inv = horse.getInventory();
        inv.setSaddle(new ItemStack(Material.SADDLE));
        horse.setOwner(p);
        horse.setDomestication(horse.getMaxDomestication());
        horse.setAdult();
        return horse;
    }

    public static ItemStack getIcon(FileManager config, String path) {
        if (config.isSet(path + ".icon.value")) {
            return new ItemUtils(XMaterial.PLAYER_HEAD)
                    .setTexture(config.get(path + ".icon.value"))
                    .setDisplayName(config.get(path + ".icon.meta.display-name"))
                    .setLore(config.get(path + ".icon.meta.lore")).build();
        } else {
            if (config.isSet(path + ".icon.material")) {
                return new ItemUtils(XMaterial.matchXMaterial(config.get(path + ".icon.material")).orElse(XMaterial.STONE_PICKAXE))
                        .setDisplayName(config.get(path + ".icon.meta.display-name"))
                        .setLore(config.get(path + ".icon.meta.lore")).build();
            } else if (config.isSet(path + ".icon")) {
                return config.getConfig().getItemStack(path + ".icon");
            }
            return config.getConfig().getItemStack(path + ".item");
        }
    }

    public static String getProgressBar(int current, int max) {
        float percent = (float) current / max;
        double por = percent * 100;
        return new DecimalFormat("####.#").format(por);
    }

    public static String getProgressBar(int current, int max, int totalBars) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);
        int leftOver = (totalBars - progressBars);
        StringBuilder sb = new StringBuilder();
        StringBuilder in = new StringBuilder();
        StringBuilder out = new StringBuilder();
        int a = 0;
        for (int i = 0; i < progressBars; i++) {
            if (a >= totalBars) {
                break;
            }
            in.append(plugin.getLang().get("progressBar.symbol"));
            a++;
        }
        for (int i = 0; i < leftOver; i++) {
            if (a >= totalBars) {
                break;
            }
            out.append(plugin.getLang().get("progressBar.symbol"));
            a++;
        }
        sb.append(plugin.getLang().get("progressBar.in"));
        sb.append(in.toString());
        sb.append(plugin.getLang().get("progressBar.out"));
        sb.append(out.toString());
        double por = percent * 100;
        String p = new DecimalFormat("####.#").format(por);
        return plugin.getLang().get("progressBar.finish").replaceAll("<progress>", sb.toString()).replaceAll("<percent>", p);
    }
}
