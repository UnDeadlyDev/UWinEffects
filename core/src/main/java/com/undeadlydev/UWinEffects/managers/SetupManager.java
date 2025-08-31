package com.undeadlydev.UWinEffects.managers;


import com.undeadlydev.UWinEffects.superclass.UltraInventory;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class SetupManager {
    private final HashMap<UUID, UltraInventory> setupInventory = new HashMap<>();

    public void setSetupInventory(Player p, UltraInventory a) {
        setupInventory.put(p.getUniqueId(), a);
    }

    public UltraInventory getSetupInventory(Player p) {
        return setupInventory.get(p.getUniqueId());
    }

    public boolean isSetupInventory(Player p) {
        return setupInventory.containsKey(p.getUniqueId());
    }

    public void removeInventory(Player p) {
        setupInventory.remove(p.getUniqueId());
    }


}