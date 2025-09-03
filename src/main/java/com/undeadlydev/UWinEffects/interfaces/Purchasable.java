package com.undeadlydev.UWinEffects.interfaces;

public interface Purchasable {

    String getPermission();

    String getAutoGivePermission();

    int getPrice();

    boolean isBuy();

    boolean needPermToBuy();

}