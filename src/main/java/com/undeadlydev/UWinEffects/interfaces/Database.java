package com.undeadlydev.UWinEffects.interfaces;

import com.undeadlydev.UWinEffects.calls.CallBackAPI;
import com.undeadlydev.UWinEffects.data.DBPlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public interface Database {

    void loadPlayer(Player p);

    void savePlayer(Player p);

    void saveAll(CallBackAPI<Boolean> done);

    void savePlayerSync(UUID uuid);

    HashMap<UUID, DBPlayer> getPlayers();

    void close();

    void createPlayer(UUID uuid, String name, DBPlayer ps);

    Connection getConnection() throws SQLException;

    DBPlayer getDBPlayer(Player p);

    void savePlayerRemove(Player p);

}