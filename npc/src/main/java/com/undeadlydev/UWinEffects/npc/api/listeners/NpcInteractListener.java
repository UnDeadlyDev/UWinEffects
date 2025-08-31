package com.undeadlydev.UWinEffects.npc.api.listeners;

import com.undeadlydev.UWinEffects.npc.api.events.NpcInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class NpcInteractListener implements Listener
{
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onClick(NpcInteractEvent event)
    {
        if(event.getNpc().getClickEvent() != null)
            event.getNpc().getClickEvent().call(event);
    }
}