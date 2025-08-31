package com.undeadlydev.UWinEffects.npc.api.listeners;

import com.undeadlydev.UWinEffects.npc.api.NpcApi;
import com.undeadlydev.UWinEffects.npc.api.manager.NpcManager;
import com.undeadlydev.UWinEffects.npc.api.objects.NPC;
import com.undeadlydev.UWinEffects.npc.api.utils.PacketReader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ConnectionListener implements Listener
{
    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        PacketReader.inject(event.getPlayer());

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                NpcManager.getList().forEach(npc -> npc.showNPCToPlayer(event.getPlayer()));
            }
        }.runTaskLater(NpcApi.plugin, 10L);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        PacketReader.uninject(event.getPlayer());

        for(NPC npc : NpcManager.getList())
            npc.hideNpcFromPlayer(event.getPlayer());
    }
}
