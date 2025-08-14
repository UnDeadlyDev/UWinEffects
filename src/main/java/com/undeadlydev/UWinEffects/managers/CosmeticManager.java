package com.undeadlydev.UWinEffects.managers;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.cosmetics.*;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import com.undeadlydev.UWinEffects.superclass.Cosmetic;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class CosmeticManager {

    private final HashMap<Integer, UWinEffect> winEffects = new HashMap<>();

    public HashMap<UUID, WinEffect> winEffectsTask = new HashMap<>();

    private final HashMap<String, Integer> lastPage = new HashMap<>();
    private final Main plugin;

    public CosmeticManager(Main plugin) {
        this.plugin = plugin;
    }

    public int getLastPage(String type) {
        return lastPage.getOrDefault(type, 0);
    }

    public void setLastPage(String type, int lastPage) {
        if (getLastPage(type) < lastPage) {
            this.lastPage.put(type, lastPage);
        }
    }

    public void reload() {
        winEffects.clear();

        if (plugin.getWineffect().isSet("wineffects")) {
            ConfigurationSection conf = plugin.getWineffect().getConfig().getConfigurationSection("wineffects");
            for (String c : conf.getKeys(false)) {
                int id = plugin.getWineffect().getInt("wineffects." + c + ".id");
                winEffects.put(id, new UWinEffect(plugin, "wineffects." + c));
                plugin.sendDebugMessage("§aWinEffect §b" + c + "§a loaded correctly.");
            }
        }
    }

    public UWinEffect getWinEffectByItem(ItemStack item) {
        if (!NBTEditor.contains(item, NBTEditor.CUSTOM_DATA, "UWINEFFECTS", "WINEFFECT")) {
            return null;
        }
        int id = NBTEditor.getInt(item, NBTEditor.CUSTOM_DATA, "UWINEFFECTS", "WINEFFECT");
        return winEffects.getOrDefault(id, null);
    }

    public int getWinEffectsSize() {
        return winEffects.size();
    }

    public HashMap<Integer, UWinEffect> getWinEffects() {
        return winEffects;
    }

    public String getSelected(int id, HashMap<Integer, ? extends Cosmetic> map) {
        if (map.containsKey(id)) {
            return map.get(id).getName();
        }
        return plugin.getLang().get("messages.noSelected");
    }

    public void addWinEffects(UUID executer, WinEffect wd) {
        winEffectsTask.put(executer, wd);
    }

    public void executeWinEffect(Player p, int id) {
        UWinEffect uwe = winEffects.get(id);
        if (uwe == null || uwe.getType().equals("none")) {
            return;
        }
        WinEffect we;
        switch (uwe.getType()) {
            case "fireworks":
                we = new WinEffectFireworks();
                break;
            case "destroyisland":
                we = new WinEffectDestroyIsland();
                break;
            case "vulcanwool":
                we = new WinEffectVulcanWool();
                break;
            case "vulcanfire":
                we = new WinEffectVulcanFire();
                break;
            case "icewalker":
                we = new WinEffectIceWalker();
                break;
            case "notes":
                we = new WinEffectNotes();
                break;
            case "chickens":
                we = new WinEffectChicken();
                break;
            case "guardian":
                we = new WinEffectWardens();
                break;
            case "daredevil":
                we = new WinEffectDareDevil();
                break;
            case "meteors":
                we = new WinEffectMeteors();
                break;
            case "pigland":
                we = new WinEffectPigLand();
                break;
            case "wolfs":
                we = new WinEffectWolfs();
                break;
            case "anvilland":
                we = new WinEffectAnvilLand();
                break;
            case "twerk":
                we = new WinEffectTwerkApocalypse();
                break;
            case "titan":
                we = new WinEffectTitan();
                break;
            case "sonicboom":
                we = new WinEffectSonicBoom();
                break;
            default:
                we = new WinEffectFireworks();
                break;
        }
        we.loadCustoms(plugin, "wineffects." + uwe.getType());
        we.start(p);
        addWinEffects(p.getUniqueId(), we);
    }
}