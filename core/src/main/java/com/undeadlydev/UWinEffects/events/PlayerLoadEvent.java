package com.undeadlydev.UWinEffects.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLoadEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Player player;
    private boolean isCancelled = false;
  
    public PlayerLoadEvent(Player player) {
        this.player = player;
    }
  
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
  
    public Player getPlayer() {
        return this.player;
    }
  
    public boolean isCancelled() {
        return this.isCancelled;
    }
  
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
  
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
