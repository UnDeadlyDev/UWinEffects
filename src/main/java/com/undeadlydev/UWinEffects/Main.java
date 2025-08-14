package com.undeadlydev.UWinEffects;

import com.google.gson.Gson;
import com.undeadlydev.UWinEffects.cmds.UwinEffectsCMD;
import com.undeadlydev.UWinEffects.cmds.winEffectsCMD;
import com.undeadlydev.UWinEffects.data.MySQLDatabase;
import com.undeadlydev.UWinEffects.interfaces.Database;
import com.undeadlydev.UWinEffects.listeners.MenuListener;
import com.undeadlydev.UWinEffects.listeners.PlayerListener;
import com.undeadlydev.UWinEffects.managers.*;
import com.undeadlydev.UWinEffects.menus.UltraInventoryMenu;
import com.undeadlydev.UWinEffects.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class Main extends JavaPlugin {

    private static Main instance;
    private Database db;
    private Gson gson;
    private FileManager wineffect, menus, lang;
    private AddonManager adm;
    private CosmeticManager cos;
    private UltraInventoryMenu uim;
    private SetupManager sm;
    private ShopManager shm;

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

    public AddonManager getAdm() {
        return this.adm;
    }

    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults(true);
        saveConfig();
        lang = new FileManager("lang", true);
        wineffect = new FileManager("cosmetics/wineffect", false);
        menus = new FileManager("menus", false);
        adm = new AddonManager();
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

    public void sendDebugMessage(String... s) {
        if (true) {
            for (String st : s) {
                Bukkit.getConsoleSender().sendMessage("§b[UWE Debug] §8| §e" + st);
            }
        }
    }

    public void reload() {
        reloadConfig();
        lang.reload();
        menus.reload();
        adm.reload();
        uim.loadMenus();
        cos.reload();
    }

    public void reloadLang() {
        lang.reload();
    }
}
