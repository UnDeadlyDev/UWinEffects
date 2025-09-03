package com.undeadlydev.UWinEffects.superclass;


import com.undeadlydev.UWinEffects.interfaces.Purchasable;
import com.undeadlydev.UWinEffects.managers.FileManager;

public abstract class Cosmetic implements Purchasable {

    public String name, permission, autoGivePermission;
    public boolean isBuy, needPermToBuy;
    public int id, slot, page, price;

    public Cosmetic(FileManager config, String path, String type) {
        this.name = config.get(path + ".name");
        this.id = config.getInt(path + ".id");
        this.slot = config.getInt(path + ".slot");
        this.page = config.getInt(path + ".page");
        this.price = config.getInt(path + ".price");
        this.permission = config.get(path + ".permission");
        this.autoGivePermission = config.getOrDefault(path + ".autoGivePermission", "uwineffects." + type + ".autogive." + name);
        this.isBuy = config.getBoolean(path + ".isBuy");
        this.needPermToBuy = config.getBooleanOrDefault(path + ".needPermToBuy", false);
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public String getAutoGivePermission() {
        return autoGivePermission;
    }

    @Override
    public boolean isBuy() {
        return isBuy;
    }

    @Override
    public boolean needPermToBuy() {
        return needPermToBuy;
    }

    @Override
    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public int getSlot() {
        return slot;
    }

    public int getPage() {
        return page;
    }

    public int getId() {
        return id;
    }
}