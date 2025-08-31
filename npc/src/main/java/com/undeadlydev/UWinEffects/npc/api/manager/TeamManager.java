package com.undeadlydev.UWinEffects.npc.api.manager;

import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamManager
{
    private static final Map<UUID, Map<String, PlayerTeam>> teams = new HashMap<>();

    public static @NotNull PlayerTeam create(@NotNull Player player, @NotNull String name)
    {
        if(exists(player, name))
            return teams.get(player.getUniqueId()).get(name);

        Scoreboard scoreboard = ((CraftScoreboard) player.getScoreboard()).getHandle();

        PlayerTeam team = new PlayerTeam(scoreboard, name);

        var map = teams.getOrDefault(player.getUniqueId(), new HashMap<>());
        map.put(name, team);
        teams.put(player.getUniqueId(), map);

        return team;
    }

    public static boolean exists(@NotNull Player player, @NotNull String name)
    {
        return teams.getOrDefault(player.getUniqueId(), new HashMap<>()).containsKey(name);
    }
}
