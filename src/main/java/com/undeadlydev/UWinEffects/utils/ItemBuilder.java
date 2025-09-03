package com.undeadlydev.UWinEffects.utils;

import com.cryptomorin.xseries.XAttribute;
import com.cryptomorin.xseries.XMaterial;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.calls.CallBackAPI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    public static ItemStack item(XMaterial material, int n, String displayName, String s) {
        ItemStack itemStack = new ItemStack(material.parseMaterial(), n, material.getData());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(s.isEmpty() ? new ArrayList<>() : Arrays.asList(s.split("\\n")));
        itemMeta.setUnbreakable(true);
        itemMeta.addEnchant(Enchantment.LURE, 1, true);
        itemStack.setItemMeta(itemMeta);
        setFlags(itemStack); // Llama al método aquí
        return itemStack;
    }

    public static ItemStack item(XMaterial material, int n, String displayName, List<String> s) {
        ItemStack itemStack = new ItemStack(material.parseMaterial(), n, material.getData());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(s);
        itemStack.setItemMeta(itemMeta);
        setFlags(itemStack); // Llama al método aquí
        return itemStack;
    }

    public static ItemStack item(XMaterial material, String displayName, String s) {
        ItemStack itemStack = new ItemStack(material.parseMaterial(), 1, material.getData());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(s.isEmpty() ? new ArrayList<>() : Arrays.asList(s.split("\\n")));
        itemStack.setItemMeta(itemMeta);
        setFlags(itemStack); // Llama al método aquí
        return itemStack;
    }

    public static ItemStack parse(ItemStack item, CallBackAPI<String> done, String[]... t) {
        ItemStack i = item.clone();
        String display = (i.hasItemMeta() && i.getItemMeta().hasDisplayName()) ? i.getItemMeta().getDisplayName() : "";
        ItemMeta im = i.getItemMeta();
        for (String[] s : t) {
            String s1 = s[0];
            String s2 = s[1];
            String s3 = s[2];
            if (display.equals(s1)) {
                done.done(s1);
                im.setDisplayName(display.replace(s1, s2));
                im.setLore(s3.isEmpty() ? new ArrayList<>() : Arrays.asList(s3.split("\\n")));
                break;
            }
        }
        i.setItemMeta(im);
        setFlags(i);
        return i;
    }

    public static ItemStack nameLore(ItemStack itemStack, String displayName, String s) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(null);
        itemMeta.setLore(s.isEmpty() ? new ArrayList<>() : Arrays.asList(s.split("\\n")));
        itemStack.setItemMeta(itemMeta);
        setFlags(itemStack);
        return itemStack;
    }

    public static ItemStack skull(XMaterial material, int n, String displayName, String s, String owner) {
        return skull(material, n, displayName, s.isEmpty() ? new ArrayList<>() : Arrays.asList(s.split("\\n")), owner);
    }

    public static ItemStack skull(XMaterial material, int n, String displayName, List<String> s, String owner) {
        ItemStack itemStack = new ItemStack(material.parseMaterial(), n, material.getData());
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
        skullMeta.setDisplayName(displayName);
        skullMeta.setLore(s);
        itemStack.setItemMeta(skullMeta);
        setFlags(itemStack);
        return itemStack;
    }

    public static void setFlags(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.values());
        try {
            meta.removeItemFlags(ItemFlag.valueOf("HIDE_LORE"));
        } catch (IllegalArgumentException e) {
            // ignored
        }
        if (!meta.hasAttributeModifiers()) {
            // Add a dummy attribute modifier. If the only attribute modifiers present are the default ones, it won't
            // actually hide them when we ask using ItemFlags.
            AttributeModifier modifier = createAttributeModifier("itemflags", 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
            meta.addAttributeModifier(XAttribute.KNOCKBACK_RESISTANCE.get(), modifier);
        }
        item.setItemMeta(meta);
    }

    @SuppressWarnings({"UnstableApiUsage", "removal"})
    public static AttributeModifier createAttributeModifier(String modName, double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        NamespacedKey key = new NamespacedKey(Main.get(), modName);
        try {
            return new AttributeModifier(key, amount, operation, slot == null ? EquipmentSlotGroup.ANY : slot.getGroup());
        } catch (NoSuchMethodError error) {
            return new AttributeModifier(UUID.randomUUID(), key.toString(), amount, operation, slot);
        }
    }
}