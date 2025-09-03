package com.undeadlydev.UWinEffects.inventories.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryAction {
  private final InventoryClickEvent inventoryClickEvent;
  
  private final Player player;
  
  public InventoryClickEvent getInventoryClickEvent() {
    return this.inventoryClickEvent;
  }
  
  public Player getPlayer() {
    return this.player;
  }
  
  public InventoryAction(InventoryClickEvent inventoryClickEvent, Player player) {
    this.inventoryClickEvent = inventoryClickEvent;
    this.player = player;
  }
}
