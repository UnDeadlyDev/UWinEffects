package com.undeadlydev.UWinEffects.addons.placeholders;

import com.undeadlydev.UWinEffects.interfaces.PlaceholderAddon;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderAPIAddon implements PlaceholderAddon {

    public String parsePlaceholders(Player p, String value) {
        return PlaceholderAPI.setPlaceholders(p, value);
    }

}