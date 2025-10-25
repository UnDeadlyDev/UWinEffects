package com.undeadlydev.UWinEffects;

import com.cryptomorin.xseries.XSound;

import com.google.gson.Gson;
import com.undeadlydev.UWinEffects.cmds.UwinEffectsCMD;
import com.undeadlydev.UWinEffects.cmds.winEffectsCMD;
import com.undeadlydev.UWinEffects.data.MySQLDatabase;
import com.undeadlydev.UWinEffects.enums.CustomSound;
import com.undeadlydev.UWinEffects.interfaces.Collision;
import com.undeadlydev.UWinEffects.interfaces.Database;
import com.undeadlydev.UWinEffects.listeners.MenuListener;
import com.undeadlydev.UWinEffects.listeners.PlayerListener;
import com.undeadlydev.UWinEffects.managers.*;
import com.undeadlydev.UWinEffects.menus.UltraInventoryMenu;
import com.undeadlydev.UWinEffects.superclass.SpigotUpdater;
import com.undeadlydev.UWinEffects.utils.ChatUtils;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class Main extends JavaPlugin {

    private static Main instance;
    private Database db;
    private Gson gson;
    private FileManager wineffect, menus, lang, sounds;
    private AddonManager adm;
    private CosmeticManager cos;
    private UltraInventoryMenu uim;
    private SetupManager sm;
    private ShopManager shm;
    private int pluginId, resourceId;
    private Collision collision;

    public static Main get() {
        return instance;
    }

    public Gson getGson() {
        return gson;
    }

    public Database getDb() {
        return db;
    }

    public FileManager getWineffect() {
        return this.wineffect;
    }

    public CosmeticManager getCos() {
        return this.cos;
    }

    public UltraInventoryMenu getUim() {
        return this.uim;
    }

    public SetupManager getSm() {
        return sm;
    }

    public ShopManager getShm() {
        return this.shm;
    }

    public FileManager getLang() {
        return lang;
    }

    public FileManager getMenus() {
        return this.menus;
    }

    public FileManager getSounds() {
        return  sounds;
    }

    public AddonManager getAdm() {
        return this.adm;
    }

    public int getResourceId() {
        return resourceId;
    }

    public Collision getCollisionAPI() {
        return collision;
    }

    @Override
    public void onEnable() {
        instance = this;
        pluginId = 26923;
        resourceId = 128009;
        getConfig().options().copyDefaults(true);
        saveConfig();
        lang = new FileManager("lang", true);
        sounds = new FileManager("sounds", true);
        menus = new FileManager("menus", false);
        wineffect = new FileManager("cosmetics/wineffect", false);

        adm = new AddonManager();
        adm.reload();

        cos = new CosmeticManager(this);
        cos.reload();

        uim = new UltraInventoryMenu(this);
        uim.loadMenus();

        shm = new ShopManager();
        sm = new SetupManager();

        new UwinEffectsCMD(this);
        new winEffectsCMD(this);

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        for (CustomSound cs : CustomSound.values()) {
            String path = "sounds." + cs.name();
            cs.setSound(((XSound)XSound.matchXSound(getSounds().get(path + ".sound")).orElse(XSound.BLOCK_NOTE_BLOCK_HAT)).parseSound());
            cs.setVolume((float)getSounds().getDouble(path + ".volume"));
            cs.setPitch((float)getSounds().getDouble(path + ".pitch"));
        }
        collision = new CollisionHelper();
        gson = new Gson();
        db = new MySQLDatabase(this);
        sendLogMessage("&7-----------------------------------");
        sendLogMessage(" ");
        sendLogMessage("&fServer: &c" + getServer().getName() + " " + getServer().getBukkitVersion());
        sendLogMessage("&fSuccessfully Plugin &aEnabled! &cv" + getDescription().getVersion());
        sendLogMessage("&fCreator: &eUnDeadlyDev");
        sendLogMessage("&fThanks for use my plugin :D");
        sendLogMessage(" ");
        sendLogMessage("&7-----------------------------------");
        loadMetrics();
        CheckUpdate();
    }

    @Override
    public void onDisable() {
        if (!getDb().getPlayers().keySet().isEmpty()) {
            Collection<UUID> tagss = new ArrayList<>(getDb().getPlayers().keySet());
            for (UUID tags : tagss)
                getDb().savePlayerSync(tags);
        }
        db.close();
        sendLogMessage("&7-----------------------------------");
        sendLogMessage(" ");
        sendLogMessage("&fSuccessfully Plugin &cDisable!");
        sendLogMessage("&fCreator: &eUnDeadlyDev");
        sendLogMessage("&fThanks for use my plugin :D");
        sendLogMessage(" ");
        sendLogMessage("&7-----------------------------------");
    }

    public void sendLogMessage(String msg) {
        Bukkit.getConsoleSender().sendMessage(ChatUtils.parseLegacy("&7[&5&lUWinEffetcs&7] &8| " + msg));
    }

    public void loadMetrics() {
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new SimplePie("placeholderapi_enabled", () -> adm.isPlaceholderAPIEnabled() ? "Yes" : "No"));
    }

    public void sendDebugMessage(String... s) {
        if (getConfig().getBoolean("debugMode")) {
            for (String st : s) {
                Bukkit.getConsoleSender().sendMessage("§b[UWE Debug] §8| §e" + st);
            }
        }
    }

    public void reload() {
        reloadConfig();
        wineffect.reload();
        lang.reload();
        menus.reload();
        adm.reload();
        uim.loadMenus();
        cos.reload();
    }

    public void reloadLang() {
        lang.reload();
    }

    private void CheckUpdate() {
        if(getConfig().getBoolean("update-check")) {
            new BukkitRunnable() {
                public void run() {
                    SpigotUpdater updater = new SpigotUpdater(instance, resourceId);
                    try {
                        if (updater.checkForUpdates()) {
                            Bukkit.getConsoleSender().sendMessage(getLang().get("messages.notifyUpdate").replace("{CURRENT}", getDescription().getVersion()).replace("{NEW}", updater.getLatestVersion()).replace("{LINK}", updater.getResourceURL()));
                        }
                    } catch (Exception e) {
                        sendLogMessage("Failed to check for a update on spigot.");
                    }
                }
            }.runTask(this);
        }
    }
}
