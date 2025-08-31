package com.undeadlydev.UWinEffects.npc.api.listeners;

import com.undeadlydev.UWinEffects.npc.api.manager.NpcManager;
import com.undeadlydev.UWinEffects.npc.api.objects.NPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class ChangeWorldListener implements Listener
{
    @EventHandler
    public void onChange(PlayerChangedWorldEvent event)
    {
        for(NPC npc : NpcManager.getList())
            npc.showNPCToPlayer(event.getPlayer());
    }
}
