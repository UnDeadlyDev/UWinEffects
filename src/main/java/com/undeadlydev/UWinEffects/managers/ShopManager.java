package com.undeadlydev.UWinEffects.managers;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.cosmetics.UWinEffect;
import com.undeadlydev.UWinEffects.data.DBPlayer;
import com.undeadlydev.UWinEffects.enums.CustomSound;
import com.undeadlydev.UWinEffects.interfaces.Purchasable;
import org.bukkit.entity.Player;

public class ShopManager {

    public void buy(Player p, Purchasable purchasable, String name) {
        Main plugin = Main.get();
        if (!purchasable.isBuy()) {
            p.sendMessage(plugin.getLang().get(p, "messages.noBuy"));
            CustomSound.NOBUY.reproduce(p);
            return;
        }
        if (plugin.getAdm().getCoins(p) < purchasable.getPrice()) {
            p.sendMessage(plugin.getLang().get(p, "messages.noCoins"));
            CustomSound.NOBUY.reproduce(p);
            return;
        }
        DBPlayer sw = plugin.getDb().getDBPlayer(p);
        plugin.getAdm().removeCoins(p, purchasable.getPrice());
        if (purchasable instanceof UWinEffect) {
            UWinEffect k = (UWinEffect) purchasable;
            sw.addWinEffects(k.getId());
        }
        p.sendMessage(plugin.getLang().get(p, "messages.bought").replaceAll("<name>", name));
    }
}