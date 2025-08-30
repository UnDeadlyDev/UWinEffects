package com.undeadlydev.UWinEffects.managers;


import com.undeadlydev.UWinEffects.interfaces.Collision;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.Bukkit;

public class CollisionHelper implements Collision {
    private Team team = null;

    public CollisionHelper() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        team = scoreboard.getTeam("uwe_collidable");
        if (team == null) {
            team = scoreboard.registerNewTeam("uwe_collidable");
        }
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER); // Oculta name tags
        team.setPrefix(""); // Sin prefijo
        team.setSuffix(""); // Sin sufijo
    }

    @Override
    public void setCollidable(Entity entity, boolean collidable) {
        if (entity == null) return;

        if (collidable) {
            team.removeEntry(entity.getUniqueId().toString());
        } else {
            team.addEntry(entity.getUniqueId().toString());
        }
    }

    @Override
    public boolean isCollidable(Entity entity) {
        if (entity == null) return true;
        return !team.hasEntry(entity.getUniqueId().toString());
    }

    public void cleanup() {
        if (team != null) {
            team.unregister();
            team = null;
        }
    }
}