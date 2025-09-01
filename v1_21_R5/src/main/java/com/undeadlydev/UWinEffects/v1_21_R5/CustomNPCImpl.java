package com.undeadlydev.UWinEffects.v1_21_R5;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.CustomNPC;
import com.undeadlydev.UWinEffects.utils.version.ServerVersion;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CustomNPCImpl implements CustomNPC {
    private Location spawnLoc;
    private Player creator;
    private ServerPlayer nmsEntity;
    private GameProfile gameProfile;
    private int entityId;
    private boolean sneaking;
    private ServerVersion serverVersion;
    private boolean initialized;

    public CustomNPCImpl() {
        this.initialized = false;
    }

    public CustomNPCImpl(Location spawnLoc, String name, Player creator) {
        this.spawnLoc = spawnLoc;
        this.creator = creator;
        this.gameProfile = new GameProfile(UUID.randomUUID(), name);
        this.sneaking = false;
        this.serverVersion = Main.get().getVersionManager().getServerVersion();
        this.initialized = true;
        initialize(name);
    }

    private void initialize(String name) {
        if (!initialized) {
            Main.get().sendDebugMessage("§cNPC not initialized. Call createNPC with parameters first.");
            return;
        }
        try {
            MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
            ServerLevel level = ((CraftWorld) spawnLoc.getWorld()).getHandle();
            GameProfile profile = new GameProfile(gameProfile.getId(), name);

            this.nmsEntity = new ServerPlayer(server, level, profile, ClientInformation.createDefault());
            nmsEntity.absSnapTo(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ(), spawnLoc.getYaw(), spawnLoc.getPitch());
            nmsEntity.connection = new ServerGamePacketListenerImpl(server, new Connection(PacketFlow.SERVERBOUND), nmsEntity, CommonListenerCookie.createInitial(profile, true));

            this.entityId = nmsEntity.getId();

            // Configurar posición y rotación
            nmsEntity.setPos(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ());
            nmsEntity.setXRot(spawnLoc.getYaw());
            nmsEntity.setYRot(spawnLoc.getPitch());

            Main.get().sendDebugMessage("Initialized NPC: " + name + " with ID " + entityId + " at " + spawnLoc);
        } catch (Exception e) {
            Main.get().sendDebugMessage("§cFailed to initialize NPC: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize NPC", e);
        }
    }

    @Override
    public CustomNPC createNPC(Location spawnLoc, String name, Player creator) {
        this.spawnLoc = spawnLoc;
        this.creator = creator;
        this.gameProfile = new GameProfile(UUID.randomUUID(), name);
        this.sneaking = false;
        this.serverVersion = Main.get().getVersionManager().getServerVersion();
        this.initialized = true;
        initialize(name);
        return this;
    }

    @Override
    public void spawn(Player viewer) {
        if (!initialized) {
            Main.get().sendDebugMessage("§cCannot spawn uninitialized NPC");
            return;
        }
        try {
            CraftPlayer craftPlayer = (CraftPlayer) viewer;
            ServerPlayer serverPlayer = craftPlayer.getHandle();

            ClientboundPlayerInfoUpdatePacket infoPacket = new ClientboundPlayerInfoUpdatePacket(
                    ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                    nmsEntity
            );

            serverPlayer.connection.send(infoPacket);

            ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(
                    nmsEntity.getId(),
                    nmsEntity.getUUID(),
                    spawnLoc.getX(),
                    spawnLoc.getY(),
                    spawnLoc.getZ(),
                    nmsEntity.getXRot(),
                    nmsEntity.getYRot(),
                    nmsEntity.getType(),
                    0,
                    Vec3.ZERO,
                    0
            );

            serverPlayer.connection.send(addEntityPacket);

            updateMetadata(viewer);

            Main.get().sendDebugMessage("Spawned NPC for player: " + viewer.getName());
        } catch (Exception e) {
            Main.get().sendDebugMessage("§cFailed to spawn NPC for " + viewer.getName() + ": " + e.getMessage());
                    e.printStackTrace();
        }
    }

    @Override
    public void setName(String name) {
        if (!initialized) {
            Main.get().sendDebugMessage("§cCannot set name on uninitialized NPC");
            return;
        }
        try {
            gameProfile = new GameProfile(gameProfile.getId(), name);

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.getWorld().equals(spawnLoc.getWorld()) && online.getLocation().distanceSquared(spawnLoc) < 100 * 100) {
                    CraftPlayer craftPlayer = (CraftPlayer) online;
                    ServerPlayer serverPlayer = craftPlayer.getHandle();
                    ClientboundPlayerInfoUpdatePacket infoPacket = new ClientboundPlayerInfoUpdatePacket(
                            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
                            nmsEntity
                    );
                    serverPlayer.connection.send(infoPacket);
                }
            }
            Main.get().sendDebugMessage("Updated NPC name to: " + name);
        } catch (Exception e) {
            Main.get().sendDebugMessage("§cFailed to set NPC name: " + e.getMessage());
                    e.printStackTrace();
        }
    }

    @Override
    public void setPose(boolean sneaking) {
        if (!initialized) {
            Main.get().sendDebugMessage("§cCannot set pose on uninitialized NPC");
            return;
        }
        try {
            this.sneaking = sneaking;
            nmsEntity.setPose(sneaking ? Pose.CROUCHING : Pose.STANDING);
            updateMetadata();
            Main.get().sendDebugMessage("Set NPC pose to " + (sneaking ? "CROUCHING" : "STANDING"));
        } catch (Exception e) {
            Main.get().sendDebugMessage("§cFailed to set NPC pose: " + e.getMessage());
                    e.printStackTrace();
        }
    }

    @Override
    public boolean isSneaking() {
        return sneaking;
    }

    private void updateMetadata() {
        if (!initialized) {
            Main.get().sendDebugMessage("§cCannot update metadata on uninitialized NPC");
            return;
        }
        try {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.getWorld().equals(spawnLoc.getWorld()) && online.getLocation().distanceSquared(spawnLoc) < 100 * 100) {
                    updateMetadata(online);
                }
            }
        } catch (Exception e) {
            Main.get().sendDebugMessage("§cFailed to update NPC metadata: " + e.getMessage());
                    e.printStackTrace();
        }
    }

    private void updateMetadata(Player viewer) {
        if (!initialized) {
            Main.get().sendDebugMessage("§cCannot update metadata on uninitialized NPC");
            return;
        }
        try {
            CraftPlayer craftPlayer = (CraftPlayer) viewer;
            ServerPlayer serverPlayer = craftPlayer.getHandle();
            ClientboundSetEntityDataPacket dataPacket = new ClientboundSetEntityDataPacket(
                    nmsEntity.getId(),
                    nmsEntity.getEntityData().getNonDefaultValues()
            );
            serverPlayer.connection.send(dataPacket);
        } catch (Exception e) {
            Main.get().sendDebugMessage("§cFailed to send metadata to " + viewer.getName() + ": " + e.getMessage());
                    e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        if (!initialized) {
            Main.get().sendDebugMessage("§cCannot destroy uninitialized NPC");
            return;
        }
        try {
            ClientboundRemoveEntitiesPacket removePacket = new ClientboundRemoveEntitiesPacket(nmsEntity.getId());

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.getWorld().equals(spawnLoc.getWorld()) && online.getLocation().distanceSquared(spawnLoc) < 100 * 100) {
                    CraftPlayer craftPlayer = (CraftPlayer) online;
                    ServerPlayer serverPlayer = craftPlayer.getHandle();
                    serverPlayer.connection.send(removePacket);
                }
            }
            nmsEntity = null;
            initialized = false;
            Main.get().sendDebugMessage("Destroyed NPC at " + spawnLoc);
        } catch (Exception e) {
            Main.get().sendDebugMessage("§cFailed to destroy NPC: " + e.getMessage());
                    e.printStackTrace();
        }
    }
}