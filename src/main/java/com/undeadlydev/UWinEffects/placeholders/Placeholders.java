package com.undeadlydev.UWinEffects.placeholders;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.data.DBPlayer;
import com.undeadlydev.UWinEffects.utils.Utils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Placeholders extends PlaceholderExpansion {

    public Placeholders() {

    }

    @Nonnull
    public String getIdentifier() {
        return "uwineffects";
    }

    @Nonnull
    public String getAuthor() {
        return "UnDeadlyDev";
    }

    @Nonnull
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, @Nonnull String id) {
        if (p == null || !p.isOnline())
            return null;
        Main plugin = Main.get();
        DBPlayer sw = plugin.getDb().getDBPlayer(p);
        if (id.equals("total_wineffects")) {
            return "" + plugin.getCos().getWinEffectsSize();
        }
        if (sw == null) {
            return "";
        }
        if (id.equals("selected_wineffect")) {
            return plugin.getCos().getSelected(sw.getWinEffect(), plugin.getCos().getWinEffects());
        }
        if (id.equals("unlocked_wineffects")) {
            return "" + sw.getWineffects().size();
        }
        if (id.equals("bar_wineffects")) {
            return "" + Utils.getProgressBar(sw.getWineffects().size(), plugin.getCos().getWinEffectsSize(), plugin.getConfig().getInt("progressBarAmount"));
        }
        if (id.equals("percentage_wineffects")) {
            return "" + Utils.getProgressBar(sw.getWineffects().size(), plugin.getCos().getWinEffectsSize());
        }
        return null;
    }
}
