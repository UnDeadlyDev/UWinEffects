package com.undeadlydev.UWinEffects.enums;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public enum Permission {

    ADMIN("admin"),

    UPDATE_CHECK("updatecheck"),

    COMMAND_MENU("command.menu");

    private static final String BASE_PERMISSION = "uwineffects.";

    private final String permissionNode;

    Permission(String permissionNode) {
        this.permissionNode = permissionNode;
    }

    public String get() {
        return BASE_PERMISSION + this.permissionNode;
    }

    public boolean has(Player player) {
        return player.hasPermission(get());
    }
}