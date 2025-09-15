package com.undeadlydev.UWinEffects.data;

import com.undeadlydev.UWinEffects.Main;

import java.util.ArrayList;
import java.util.List;

public class DBPlayer {

    //WINEFFECTS

    private List<Integer> wineffects = new ArrayList<>();

    private int winEffect = Main.get().getConfig().getInt("config.defaultWinEffect");

    public List<Integer> getWineffects() {
        return this.wineffects;
    }

    public void setWineffects(List<Integer> wineffects) {
        this.wineffects = wineffects;
    }

    public void addWinEffects(int id) {
        this.wineffects.add(Integer.valueOf(id));
    }

    public void removeWinEffects(int id) {
        this.wineffects.remove(id);
    }

    public int getWinEffect() {
        return this.winEffect;
    }

    public void setWinEffect(int winEffect) {
        this.winEffect = winEffect;
    }


    public void addCoins(int coins) {
        this.coins += coins;
    }

    public void removeCoins(int coins) {
        this.coins -= coins;
    }

    public int getCoins() {
        return this.coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    private int coins = 0;


}