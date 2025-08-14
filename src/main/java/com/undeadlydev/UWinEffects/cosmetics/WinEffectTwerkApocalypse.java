package com.undeadlydev.UWinEffects.cosmetics;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class WinEffectTwerkApocalypse implements WinEffect {

    private final ArrayList<NPC> npcs1 = new ArrayList<>();
    private final ArrayList<NPC> npcs2 = new ArrayList<>();
    private BukkitTask task;

    @Override
    public void start(Player p) {
        for (int i = 0; i < 10; i++) {
            Location randomLocation = p.getLocation().add(random(-10, 10), 0, random(-10, 10));
            NPC npc = spawnNPC1(p, randomLocation);
            npcs1.add(npc);
        }
        for (int i = 0; i < 10; i++) {
            Location randomLocation = p.getLocation().add(random(-10, 10), 0, random(-10, 10));
            NPC npc = spawnNPC2(p, randomLocation);
            npcs2.add(npc);
        }
        task = new BukkitRunnable() {
            final String name = p.getWorld().getName();
            @Override
            public void run() {
                if (p == null || !p.isOnline() || !name.equals(p.getWorld().getName())) {
                    stop();
                    return;
                }
                if (!npcs1.isEmpty()) {
                    for (NPC npc : npcs1) {
                        Player npcPlayer = (Player) npc.getEntity();
                        npcPlayer.setSneaking(!npcPlayer.isSneaking());
                    }
                    for (NPC npc1 : npcs2) {
                        Player npcPlayer = (Player) npc1.getEntity();
                        npcPlayer.setSneaking(!npcPlayer.isSneaking());
                    }
                }
            }
        }.runTaskTimer(Main.get(), 0, 10);
    }

    @Override
    public void stop() {
        for (NPC npc : npcs1) {
            npc.despawn();
            npc.destroy();
        }
        for (NPC npc : npcs2) {
            npc.despawn();
            npc.destroy();
        }
        npcs1.clear();
        npcs2.clear();
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    public WinEffect clone() {
        return new WinEffectTwerkApocalypse();
    }

    private NPC spawnNPC1(Player p, Location randomLocation) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, p.getPlayer().getName());
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent(p);
        npc.setName(p.getName());
        npc.setSneaking(true);
        npc.setFlyable(true);
        npc.setProtected(true);
        npc.spawn(randomLocation);
        return npc;
    }

    private NPC spawnNPC2(Player p, Location randomLocation) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, p.getPlayer().getName());
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent(p);
        npc.setName(p.getName());
        npc.setFlyable(true);
        npc.setProtected(true);
        npc.spawn(randomLocation);
        return npc;
    }

    protected double random(double min, double max) {
        return min + ThreadLocalRandom.current().nextDouble() * (max - min);
    }

    @Override
    public void loadCustoms(Main plugin, String path) {}
}
