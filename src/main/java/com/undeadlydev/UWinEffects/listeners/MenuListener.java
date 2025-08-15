package com.undeadlydev.UWinEffects.listeners;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.inventories.actions.InventoryAction;
import com.undeadlydev.UWinEffects.superclass.UltraInventory;
import com.undeadlydev.UWinEffects.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MenuListener implements Listener {

    private Main plugin;

    public MenuListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Main plugin = Main.get();
        Player p = (Player) e.getPlayer();
        if (plugin.getSm().isSetupInventory(p)) {
            UltraInventory i = plugin.getSm().getSetupInventory(p);
            plugin.getUim().setInventory(i.getName(), e.getInventory());
            plugin.getSm().removeInventory(p);
            plugin.getUim().loadMenus();
            p.sendMessage(ChatUtils.colorCodes("&aInventory Saved."));
        }
        if (plugin.getUim().getViews().containsKey(p.getUniqueId())) {
            plugin.getUim().getViews().remove(p.getUniqueId());
        }
    }

    @EventHandler
    public void onMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Main plugin = Main.get();
        if (plugin.getUim().getActions().containsKey(e.getView().getTitle())) {
            if (plugin.getSm().isSetupInventory(p)) return;
            plugin.getUim().getActions().get(e.getView().getTitle()).accept(new InventoryAction(e, p));
            return;
        }
    }
}