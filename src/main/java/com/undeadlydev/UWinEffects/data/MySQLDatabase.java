package com.undeadlydev.UWinEffects.data;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.calls.CallBackAPI;
import com.undeadlydev.UWinEffects.events.PlayerLoadEvent;
import com.undeadlydev.UWinEffects.interfaces.Database;
import com.undeadlydev.UWinEffects.utils.Utils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQLDatabase implements Database {

    private final static String SAVE = "UPDATE uwineffects_data SET Data=? WHERE UUID=?";
    private final static HashMap<UUID, DBPlayer> players = new HashMap<>();
    private static boolean enabled;
    private final Main plugin;
    private HikariDataSource hikari;

    public MySQLDatabase(Main plugin) {
        this.plugin = plugin;
        enabled = plugin.getConfig().getBoolean("mysql.enabled");
        connect();
    }

    @Override
    public HashMap<UUID, DBPlayer> getPlayers() {
        return players;
    }

    public void connect() {
        if (enabled) {
            int port = plugin.getConfig().getInt("mysql.port");
            String ip = plugin.getConfig().getString("mysql.host");
            String database = plugin.getConfig().getString("mysql.database");
            String username = plugin.getConfig().getString("mysql.username");
            String password = plugin.getConfig().getString("mysql.password");
            HikariConfig config = new HikariConfig();
            String connectionString = "jdbc:mysql://" + ip + ":" + port + "/" + database + "?autoReconnect=true";
            config.setJdbcUrl(connectionString);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName("com.mysql.jdbc.Driver");
            config.addDataSourceProperty("cachePrepStmts", true);
            config.addDataSourceProperty("prepStmtCacheSize", 250);
            config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
            config.addDataSourceProperty("useServerPrepStmts", true);
            config.addDataSourceProperty("useLocalSessionState", true);
            config.addDataSourceProperty("rewriteBatchedStatements", true);
            config.addDataSourceProperty("cacheResultSetMetadata", true);
            config.addDataSourceProperty("cacheServerConfiguration", true);
            config.addDataSourceProperty("elideSetAutoCommits", true);
            config.addDataSourceProperty("maintainTimeStats", false);
            config.addDataSourceProperty("characterEncoding", "utf8");
            config.addDataSourceProperty("encoding", "UTF-8");
            config.addDataSourceProperty("useUnicode", "true");
            config.addDataSourceProperty("useSSL", false);
            config.addDataSourceProperty("tcpKeepAlive", true);
            config.setPoolName("UWE " + UUID.randomUUID().toString());
            config.setMaxLifetime(Long.MAX_VALUE);
            config.setMinimumIdle(0);
            config.setIdleTimeout(30000L);
            config.setConnectionTimeout(10000L);
            config.setMaximumPoolSize(10);
            hikari = new HikariDataSource(config);
            createTable();
            plugin.getLogger().info("MySQL connected successfully.");
        } else {
            try (Connection conn = getConnection()) {
                createTable();
                plugin.getLogger().info("SQLite connected correctly.");
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
    }

    public void close() {
        if (enabled && hikari != null) {
            hikari.close();
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS uwineffects_data(UUID varchar(36) primary key, Name varchar(20), Data LONGTEXT, UNIQUE(UUID));";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createPlayer(UUID uuid, String name, DBPlayer ps) {
        try (Connection conn = getConnection()) {
            String sql = enabled
                    ? "INSERT INTO uwineffects_data VALUES(?,?,?) ON DUPLICATE KEY UPDATE Name=?"
                    : "INSERT OR REPLACE INTO uwineffects_data(UUID, Name, Data) VALUES(?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, uuid.toString());
                stmt.setString(2, name);
                stmt.setString(3, Utils.toGson(ps));
                if (enabled) stmt.setString(4, name);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadPlayer(final Player p) {
        CompletableFuture.runAsync(() -> {
            try (Connection conn = getConnection()) {
                try (PreparedStatement select = conn.prepareStatement("SELECT Data FROM uwineffects_data WHERE UUID=?")) {
                    select.setString(1, p.getUniqueId().toString());
                    try (ResultSet rs = select.executeQuery()) {
                        if (rs.next()) {
                            String data = rs.getString("Data");
                            addPlayer(p, data != null ? Utils.fromGson(data) : new DBPlayer());
                            return;
                        }
                    }
                }
                try (PreparedStatement insert = conn.prepareStatement("INSERT INTO uwineffects_data (UUID, Name, Data) VALUES (?, ?, ?);")) {
                    insert.setString(1, p.getUniqueId().toString());
                    insert.setString(2, p.getName());
                    insert.setString(3, Utils.toGson(new DBPlayer()));
                    insert.executeUpdate();
                    addPlayer(p, new DBPlayer());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        });
    }

    @Override
    public void savePlayer(final Player p) {
        DBPlayer ps = players.get(p.getUniqueId());
        if (ps == null) return;
        CompletableFuture.runAsync(() -> saveValues(ps, p.getUniqueId()));
    }

    @Override
    public void savePlayerRemove(final Player p) {
        DBPlayer ps = players.get(p.getUniqueId());
        if (ps == null) return;
        CompletableFuture.runAsync(() -> {
            saveValues(ps, p.getUniqueId());
            removePlayer(p.getUniqueId());
        });
    }

    private void saveValues(DBPlayer ps, UUID uuid) {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(SAVE)) {
            stmt.setString(1, Utils.toGson(ps));
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveAll(CallBackAPI<Boolean> done) {
        CompletableFuture.runAsync(() -> {
            Bukkit.getOnlinePlayers().forEach(p -> {
                DBPlayer ps = players.get(p.getUniqueId());
                if (ps != null) saveValues(ps, p.getUniqueId());
            });
        });
    }

    @Override
    public void savePlayerSync(UUID uuid) {
        DBPlayer ps = players.get(uuid);
        if (ps != null) {
            saveValues(ps, uuid);
            removePlayer(uuid);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (enabled) return hikari.getConnection();
        File dbFile = new File(plugin.getDataFolder(), "sqlite.db");
        return DriverManager.getConnection("jdbc:sqlite:" + dbFile);
    }

    private void addPlayer(Player p, DBPlayer sw) {
        if (p == null || !p.isOnline()) return;
        players.put(p.getUniqueId(), sw);
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(new PlayerLoadEvent(p)));
    }

    private void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    @Override
    public DBPlayer getDBPlayer(Player p) {
        return (p != null && p.isOnline()) ? players.getOrDefault(p.getUniqueId(), new DBPlayer()) : new DBPlayer();
    }
}
