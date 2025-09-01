package com.undeadlydev.UWinEffects.v1_20_R1;

import com.undeadlydev.UWinEffects.interfaces.CustomNPC;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;


public class CustomNPCImpl implements CustomNPC {
    @Override
    public void spawn(Player viewer) {

    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void setPose(boolean sneaking) {

    }

    @Override
    public boolean isSneaking() {
        return false;
    }

    @Override
    public void destroy(List<CustomNPC> npcs) {

    }

    @Override
    public CustomNPC createNPC(Location spawnLoc, String name, Player creator) {

        return null;
    }
}
