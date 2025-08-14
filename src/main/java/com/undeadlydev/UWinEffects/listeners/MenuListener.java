package com.undeadlydev.UWinEffects.listeners;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.data.DBPlayer;
import com.undeadlydev.UWinEffects.inventories.actions.InventoryAction;
import com.undeadlydev.UWinEffects.superclass.UltraInventory;
import com.undeadlydev.UWinEffects.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

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
        DBPlayer datos = plugin.getDb().getDBPlayer(p);
        if (!e.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) {
            List<ItemStack> items = new ArrayList<>();
            items.add(e.getCurrentItem());
            items.add(e.getCursor());
            items.add((e.getClick() == ClickType.NUMBER_KEY) ? e.getWhoClicked().getInventory().getItem(e.getHotbarButton()) : e.getCurrentItem());
        }
        if (plugin.getUim().getActions().containsKey(e.getView().getTitle())) {
            plugin.getUim().getActions().get(e.getView().getTitle()).accept(new InventoryAction(e, p));
            return;
        }
    }
}