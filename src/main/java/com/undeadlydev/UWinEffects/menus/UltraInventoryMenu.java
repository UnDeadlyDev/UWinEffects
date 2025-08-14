package com.undeadlydev.UWinEffects.menus;

import com.cryptomorin.xseries.XMaterial;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.cosmetics.UWinEffect;
import com.undeadlydev.UWinEffects.data.DBPlayer;
import com.undeadlydev.UWinEffects.inventories.selectors.WinEffectsSelectorMenu;
import com.undeadlydev.UWinEffects.superclass.UltraInventory;
import com.undeadlydev.UWinEffects.inventories.actions.InventoryAction;
import com.undeadlydev.UWinEffects.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class UltraInventoryMenu {

    private ConcurrentHashMap<UUID, String> views = new ConcurrentHashMap<>();

    private HashMap<String, UltraInventory> menus = new HashMap<>();
    private HashMap<UUID, Integer> pages = new HashMap<>();
    private HashMap<String, Consumer<InventoryAction>> actions = new HashMap<>();
    private Main plugin;

    public UltraInventoryMenu(Main plugin) {
        this.plugin = plugin;
    }

    public ConcurrentHashMap<UUID, String> getViews() {
        return views;
    }

    public void loadMenus() {
        menus.clear();
        menus.put("wineffectsselector", new WinEffectsSelectorMenu(plugin, "wineffectsselector"));

        loadMenusActions();
    }

    public void openInventory(Player p, UltraInventory i) {
        Inventory inv = Bukkit.createInventory(null, i.getRows() * 9, i.getTitle());
        for (Map.Entry<Integer, ItemStack> entry : i.getConfig().entrySet()) {
            Integer s = entry.getKey();
            ItemStack it = entry.getValue();
            inv.setItem(s, it);
        }
        p.openInventory(inv);
    }

    public void setInventory(String inv, Inventory close) {
        if (menus.containsKey(inv)) {
            Map<Integer, ItemStack> items = new HashMap<>();
            for (int i = 0; i < close.getSize(); i++) {
                ItemStack it = close.getItem(i);
                if (it == null || it.getType().equals(Material.AIR)) {
                    items.put(i, XMaterial.AIR.parseItem());
                } else {
                    items.put(i, it);
                }

            }
            menus.get(inv).setConfig(items);
            menus.get(inv).save();
        }
    }

    public UltraInventory getMenus(String t) {
        return menus.get(t);
    }

    public void createWinEffectSelectorMenu(Player p) {
        int page = pages.get(p.getUniqueId());
        WinEffectsSelectorMenu selector = (WinEffectsSelectorMenu) getMenus("wineffectsselector");
        Inventory inv = Bukkit.createInventory(null, selector.getRows() * 9, plugin.getLang().get(p, "menus.wineffectsselector.title"));
        for (int s : selector.getExtra()) {
            inv.setItem(s, selector.getContents().get(s));
        }
        DBPlayer sw = plugin.getDb().getDBPlayer(p);
        for (UWinEffect k : plugin.getCos().getWinEffects().values()) {
            if (k.getId() == sw.getWinEffect()) {
                ItemStack i = k.getIcon(p);
                ItemStack kit = ItemBuilder.nameLore(i.clone(), plugin.getLang().get(p, "menus.wineffectsselector.selected.nameItem"), plugin.getLang().get(p, "menus.wineffectsselector.selected.loreItem"));
                int s = selector.getSlot("{SELECTED}");
                if (s > -1 && s < 54) {
                    inv.setItem(s, kit);
                }
                if (k.getPage() != page) continue;
                inv.setItem(k.getSlot(), i);
            } else {
                if (k.getPage() != page) continue;
                inv.setItem(k.getSlot(), k.getIcon(p));
            }
        }
        if (sw.getWinEffect() != 999999) {
            int s = selector.getSlot("{DESELECT}");
            if (s > -1 && s < 54) {
                inv.setItem(s, selector.getContents().get(s));
            }
        }
        if (page > 1) {
            int s = selector.getSlot("{LAST}");
            if (s > -1 && s < 54) {
                inv.setItem(s, selector.getContents().get(s));
            }
        }
        if (page < plugin.getCos().getLastPage("WinEffect")) {
            int s = selector.getSlot("{NEXT}");
            if (s > -1 && s < 54) {
                inv.setItem(s, selector.getContents().get(s));
            }
        }
        int s = selector.getSlot("{CLOSE}");
        if (s > -1 && s < 54) {
            inv.setItem(s, selector.getContents().get(s));
        }
        p.openInventory(inv);
    }

    public HashMap<UUID, Integer> getPages() {
        return pages;
    }

    public void addPage(Player p) {
        pages.putIfAbsent(p.getUniqueId(), 1);
        pages.put(p.getUniqueId(), pages.get(p.getUniqueId()) + 1);
    }

    public void removePage(Player p) {
        pages.put(p.getUniqueId(), pages.get(p.getUniqueId()) - 1);
    }

    public void loadMenusActions() {
        actions.put(plugin.getLang().get("menus.wineffectsselector.title"), (b) -> {
            InventoryClickEvent e = b.getInventoryClickEvent();
            Player p = b.getPlayer();
            if (plugin.getSm().isSetupInventory(p))
                return;
            String display = checkDisplayName(b);
            if (display.equals("none"))
                return;
            DBPlayer sw = plugin.getDb().getDBPlayer(p);
            if (display.equals(plugin.getLang().get(p, "menus.next.nameItem"))) {
                plugin.getUim().addPage(p);
                plugin.getUim().createWinEffectSelectorMenu(p);
                return;
            }
            if (display.equals(plugin.getLang().get(p, "menus.last.nameItem"))) {
                plugin.getUim().removePage(p);
                plugin.getUim().createWinEffectSelectorMenu(p);
                return;
            }
            if (display.equals(plugin.getLang().get(p, "menus.wineffectsselector.kit.nameItem"))) {
                return;
            }
            if (display.equals(plugin.getLang().get(p, "menus.wineffectsselector.deselect.nameItem"))) {
                if (sw.getWinEffect() == 999999) {
                    p.sendMessage(plugin.getLang().get(p, "messages.noSelect"));
                    return;
                }
                sw.setWinEffect(999999);
                p.sendMessage(plugin.getLang().get(p, "messages.deselectWinEffect"));
                plugin.getUim().createWinEffectSelectorMenu(p);
                return;
            }
            if (display.equals(plugin.getLang().get(p, "menus.wineffectsselector.close.nameItem"))) {
            	p.closeInventory();
                return;
            }
            UWinEffect k = plugin.getCos().getWinEffectByItem(e.getCurrentItem());
            if (k == null) {
                return;
            }
            if (p.hasPermission(k.getAutoGivePermission())) {
                sw.setWinEffect(k.getId());
                p.sendMessage(plugin.getLang().get(p, "messages.selectWinEffect").replaceAll("<wineffect>", k.getName()));
                plugin.getUim().createWinEffectSelectorMenu(p);
                return;
            }
            if (!sw.getWineffects().contains(k.getId())) {
                if (k.needPermToBuy() && !p.hasPermission(k.getPermission())) {
                    p.sendMessage(plugin.getLang().get(p, "messages.noPermit"));
                } else {
                    plugin.getShm().buy(p, k, k.getName());
                }
            } else {
                sw.setWinEffect(k.getId());
                p.sendMessage(plugin.getLang().get(p, "messages.selectWinEffect").replaceAll("<wineffect>", k.getName()));
            }
            plugin.getUim().createWinEffectSelectorMenu(p);
        });
    }

    public String checkDisplayName(InventoryAction b) {
        InventoryClickEvent e = b.getInventoryClickEvent();
        e.setCancelled(true);
        if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) {
            return "none";
        }
        ItemStack item = e.getCurrentItem();
        if (!item.hasItemMeta()) {
            return "none";
        }
        if (!item.getItemMeta().hasDisplayName()) {
            return "none";
        }
        ItemMeta im = item.getItemMeta();
        return im.getDisplayName();
    }

    public HashMap<String, UltraInventory> getMenus() {
        return menus;
    }

    public HashMap<String, Consumer<InventoryAction>> getActions() {
        return actions;
    }

    public void removeInventory(Player p) {
        pages.remove(p.getUniqueId());
    }

}