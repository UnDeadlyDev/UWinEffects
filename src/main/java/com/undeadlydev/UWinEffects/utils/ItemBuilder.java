package com.undeadlydev.UWinEffects.utils;

import com.cryptomorin.xseries.XMaterial;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.calls.CallBackAPI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup; // Importación correcta para 1.20.5+
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field; // Para usar reflexión
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    public static ItemStack item(XMaterial material, int n, String displayName, String s) {
        ItemStack itemStack = new ItemStack(material.parseMaterial(), n, material.getData());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        removeFlags(itemMeta);
        itemMeta.setLore(s.isEmpty() ? new ArrayList<>() : Arrays.asList(s.split("\\n")));
        itemMeta.setUnbreakable(true);
        itemMeta.addEnchant(Enchantment.LURE, 1, true);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack item(XMaterial material, int n, String displayName, List<String> s) {
        ItemStack itemStack = new ItemStack(material.parseMaterial(), n, material.getData());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        removeFlags(itemMeta);
        itemMeta.setLore(s);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack item(XMaterial material, String displayName, String s) {
        ItemStack itemStack = new ItemStack(material.parseMaterial(), 1, material.getData());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        removeFlags(itemMeta);
        itemMeta.setLore(s.isEmpty() ? new ArrayList<>() : Arrays.asList(s.split("\\n")));
        itemStack.setItemMeta(itemMeta);
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
        if (im != null) {
            removeFlags(im);
        }
        i.setItemMeta(im);
        return i;
    }

    public static ItemStack nameLore(ItemStack itemStack, String displayName, String s) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        removeFlags(itemMeta);
        itemMeta.setLore(null);
        itemMeta.setLore(s.isEmpty() ? new ArrayList<>() : Arrays.asList(s.split("\\n")));
        itemStack.setItemMeta(itemMeta);
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
        return itemStack;
    }

    public static void removeFlags(ItemMeta meta) {
        // Apply common flags directly
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES); // Hides generic attribute modifiers
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);   // Hides enchantment glint/names

        // Try to hide all tooltips with the most comprehensive flag (1.20.5+)
        try {
            meta.addItemFlags(ItemFlag.valueOf("HIDE_TOOLTIP")); // For Minecraft 1.20.5+
        } catch (IllegalArgumentException | NoSuchFieldError e) {
            // If HIDE_TOOLTIP doesn't exist (older versions), fall back to other methods
            // Duels.get().getLogger().warning("HIDE_TOOLTIP ItemFlag not found. Falling back to older methods.");
            try {
                // For 1.19+ (Paper/Spigot might have this)
                meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            } catch (IllegalAccessError | NoSuchFieldError ex) {
                // For even older versions or specific forks, try reflection for HIDE_POTION_EFFECTS
                try {
                    Field hidePotionEffects = ItemFlag.class.getDeclaredField("HIDE_POTION_EFFECTS");
                    meta.addItemFlags((ItemFlag) hidePotionEffects.get(null));
                } catch (IllegalAccessException | NoSuchFieldException reflectionEx) {
                    // Duels.get().getLogger().warning("Could not hide additional tooltips on this server version.");
                }
            }
        }

        // --- Consider these points regarding your addAttributeModifier calls ---
        // If the goal is to hide attributes that are *naturally* on the item (like Mace's damage),
        // then HIDE_ATTRIBUTES is sufficient.
        // If you are adding custom attributes *only* to hide them, you can remove these lines.
        // If these attributes (5.0D armor, 5.0D attack) are meant to be *actual stats* but just not shown in tooltip,
        // then keep them, and HIDE_ATTRIBUTES will do its job.
        try {
            NamespacedKey key1 = new NamespacedKey(Main.get(), "armor");
            addAttributeModifier(meta, Attribute.ARMOR, key1, 5.0D, Operation.ADD_NUMBER, EquipmentSlotGroup.ARMOR);
            NamespacedKey key2 = new NamespacedKey(Main.get(), "custom_attack_modifier");
            addAttributeModifier(meta, Attribute.ATTACK_DAMAGE, key2, 5.0D, Operation.ADD_NUMBER, EquipmentSlotGroup.HAND);
        } catch (NoSuchFieldError noSuchFieldError) {
            // This catches if Attribute.ARMOR or Attribute.ATTACK_DAMAGE don't exist (very old versions)
            // or if EquipmentSlotGroup doesn't exist (pre-1.20.5)
            // Duels.get().getLogger().warning("Could not add attribute modifiers due to missing fields.");
        }
    }

    public static void addAttributeModifier(ItemMeta item, Attribute attribute, NamespacedKey key, double amount, Operation operation, EquipmentSlotGroup group) {
        AttributeModifier modifier = new AttributeModifier(key, amount, operation, group); // Use key.toString() for the name
        item.addAttributeModifier(attribute, modifier);
    }
}
