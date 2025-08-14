package com.undeadlydev.UWinEffects.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ChatUtils {

    public static String colorCodes(String input) {
        return HexUtils.colorify(input);
    }

    public static String parseLegacy(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> replaceList(List<String> list) {
        List<String> localList = new ArrayList<String>();
        localList.addAll(list);
        for (int i = 0; i < localList.size(); i++) {
            localList.set(i, colorCodes(localList.get(i)));
        }
        return localList;
    }
}