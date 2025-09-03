package com.undeadlydev.UWinEffects.utils;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class ItemUtils {

    private ItemStack item;
    private ItemMeta im;

    public ItemUtils(XMaterial material) {
        this.item = new ItemStack(material.parseMaterial(), 1, material.getData());
        this.im = item.getItemMeta();
    }

    public ItemUtils setTexture(String texture) {
        try {
            SkullMeta skullMeta = (SkullMeta) im;
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), "");
            PlayerTextures textures = profile.getTextures();
            String url = new String(Base64.getDecoder().decode(texture));
            textures.setSkin(new URL(url.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(), url.length() - "\"}}}".length())));
            profile.setTextures(textures);
            skullMeta.setOwnerProfile(profile);
            return this;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemUtils setDisplayName(String displayName) {
        this.im.setDisplayName(displayName);
        return this;
    }

    public ItemUtils setLore(String lore) {
        this.im.setLore(lore.isEmpty() ? new ArrayList<>() : Arrays.asList(lore.split("\\n")));
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(im);
        ItemBuilder.setFlags(item);
        return item;
    }
}