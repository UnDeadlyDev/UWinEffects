package com.undeadlydev.UWinEffects.managers;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public enum CustomSound {

    WINEFFECTS_VULCANWOOL(null, 0.0f, 0.0f),
    WINEFFECTS_VULCANFIRE(null, 0.0f, 0.0f),
    WINEFFECTS_NOTES(null, 0.0f, 0.0f),
    WINEFFECTS_CHICKEN(null, 0.0f, 0.0f),
    WINEFFECTS_DAREDEVIL(null, 0.0f, 0.0f),
    WINEFFECTS_ICEWALKER(null, 0.0f, 0.0f),
    NOBUY(null, 0.0f, 0.0f);

    private Sound sound;
    private float volume, pitch;

    CustomSound(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void reproduce(Player p) {
        p.playSound(p.getLocation(), sound, volume, pitch);
    }
}