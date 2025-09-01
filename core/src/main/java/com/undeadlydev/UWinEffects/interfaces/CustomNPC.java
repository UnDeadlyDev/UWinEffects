package com.undeadlydev.UWinEffects.interfaces;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface CustomNPC {

    void spawn(Player viewer);
    void setName(String name);
    void setPose(boolean sneaking);
    boolean isSneaking();
    void destroy(List<CustomNPC> npcs);
    CustomNPC createNPC(Location spawnLoc, String name, Player creator);
}