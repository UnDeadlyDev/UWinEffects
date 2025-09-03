package com.undeadlydev.UWinEffects.managers;


import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.addons.economy.VaultAddon;
import com.undeadlydev.UWinEffects.addons.placeholders.PlaceholderAPIAddon;
import com.undeadlydev.UWinEffects.data.DBPlayer;
import com.undeadlydev.UWinEffects.interfaces.EconomyAddon;
import com.undeadlydev.UWinEffects.interfaces.PlaceholderAddon;
import com.undeadlydev.UWinEffects.placeholders.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AddonManager {

    private PlaceholderAddon placeholder;
    private EconomyAddon economy;

    public boolean check(String pluginName) {
        Main plugin = Main.get();
        if (plugin.getConfig().isSet("addons." + pluginName) && plugin.getConfig().getBoolean("addons." + pluginName)) {
            if (Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
                plugin.sendLogMessage("Hooked into §a" + pluginName + "§e!");
                return true;
            } else {
                plugin.getConfig().set("addons." + pluginName, false);
                plugin.saveConfig();
                return false;
            }
        }
        return false;
    }

    public void reload() {
        Main plugin = Main.get();
        if (check("PlaceholderAPI")) {
            placeholder = new PlaceholderAPIAddon();
            new Placeholders().register();
        }
        if (check("Vault")) {
            economy = new VaultAddon();
        }
    }

    public String parsePlaceholders(Player p, String value) {
        if (placeholder != null) {
            value = placeholder.parsePlaceholders(p, value);
        }
        return value;
    }

    public void addCoins(Player p, double amount) {
        Main plugin = Main.get();
        if (economy != null) {
            economy.addCoins(p, amount);
        } else {
            DBPlayer sw = plugin.getDb().getDBPlayer(p);
            if (sw == null) return;
            sw.addCoins((int) amount);
        }
    }

    public void setCoins(Player p, double amount) {
        Main plugin = Main.get();
        if (economy != null) {
            economy.setCoins(p, amount);
        } else {
            DBPlayer sw = plugin.getDb().getDBPlayer(p);
            if (sw == null) return;
            sw.setCoins((int) amount);
        }
    }

    public void removeCoins(Player p, double amount) {
        Main plugin = Main.get();
        if (economy != null) {
            economy.removeCoins(p, amount);
        } else {
            DBPlayer sw = plugin.getDb().getDBPlayer(p);
            if (sw == null) return;
            sw.removeCoins((int) amount);
        }
    }

    public double getCoins(Player p) {
        Main plugin = Main.get();
        if (economy != null) {
            return economy.getCoins(p);
        } else {
            DBPlayer sw = plugin.getDb().getDBPlayer(p);
            if (sw == null) {
                return 0;
            }
            return sw.getCoins();
        }
    }

    public boolean isPlaceholderAPIEnabled() {
        return placeholder != null;
    }
}