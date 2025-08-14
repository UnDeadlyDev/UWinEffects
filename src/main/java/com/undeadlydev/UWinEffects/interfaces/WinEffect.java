package com.undeadlydev.UWinEffects.interfaces;

import com.undeadlydev.UWinEffects.Main;
import org.bukkit.entity.Player;

public interface WinEffect {

    void start(Player p);

    void loadCustoms(Main plugin, String path);
    
    void stop();

    WinEffect clone();

}