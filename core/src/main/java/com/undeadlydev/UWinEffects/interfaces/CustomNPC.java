package com.undeadlydev.UWinEffects.interfaces;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface CustomNPC {

    void spawn(Player viewer);
    void setName(String name);
    void setPose(boolean sneaking);
    boolean isSneaking();
    void destroy();
    CustomNPC createNPC(Location spawnLoc, String name, Player creator);
}