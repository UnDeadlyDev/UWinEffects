package com.undeadlydev.UWinEffects.v1_21_R5;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.CustomNPC;
import com.undeadlydev.UWinEffects.utils.Reflections;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.UUID;

public class CustomNPCImpl implements CustomNPC {
    private Location spawnLoc;
    private Player creator;
    private ServerPlayer nmsEntity;
    private GameProfile gameProfile;
    private int entityId;
    private boolean sneaking;

    public CustomNPCImpl() {}

    @Override
    public CustomNPC createNPC(Location spawnLoc, String name, Player creator) {
        this.spawnLoc = spawnLoc;
        this.creator = creator;
        this.gameProfile = new GameProfile(UUID.randomUUID(), name);
        this.sneaking = false;

        initialize(name);
        return this;
    }

    public @NotNull ServerPlayer getServerPlayer() {
        return nmsEntity;
    }

    private void initialize(String name) {
        try {
            MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
            ServerLevel level = ((CraftWorld) spawnLoc.getWorld()).getHandle();
            this.gameProfile = new GameProfile(gameProfile.getId(), name);
            this.nmsEntity = new ServerPlayer(server, level, this.gameProfile, ClientInformation.createDefault());
            nmsEntity.absSnapTo(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ(), spawnLoc.getYaw(), spawnLoc.getPitch());
            nmsEntity.connection = new ServerGamePacketListenerImpl(server, new Connection(PacketFlow.SERVERBOUND), nmsEntity, CommonListenerCookie.createInitial(this.gameProfile, true)) {
            };

            this.entityId = nmsEntity.getId();

            nmsEntity.setPos(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ());
            nmsEntity.setXRot(spawnLoc.getPitch());
            nmsEntity.setYRot(spawnLoc.getYaw());

            Main.get().sendDebugMessage("Initialized NPC: " + name + " with ID " + entityId + " at " + spawnLoc);
        } catch (Exception e) {
            Main.get().sendDebugMessage("§cFailed to initialize NPC: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize NPC", e);
        }
    }

    @Override
    public void spawn(Player viewer) {
        if (nmsEntity == null) {
            Main.get().sendDebugMessage("§cCannot spawn uninitialized NPC");
            return;
        }
        try {
            ServerGamePacketListenerImpl connection = ((CraftPlayer) viewer).getHandle().connection;

            ClientboundPlayerInfoUpdatePacket infoPacket = new ClientboundPlayerInfoUpdatePacket(
                    ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                    nmsEntity
            );
            connection.send(infoPacket);
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
            connection.send(addEntityPacket);
            ClientboundRotateHeadPacket rotateHeadPacket = new ClientboundRotateHeadPacket(
                    nmsEntity,
                    (byte) ((spawnLoc.getYaw() % 360) * 256 / 360)
            );
            connection.send(rotateHeadPacket);
            setPose(sneaking);
            updateMetadata(viewer);

            Main.get().sendDebugMessage("Spawned NPC for player: " + viewer.getName());

        } catch (Exception e) {
            Main.get().sendDebugMessage("§cFailed to spawn NPC for " + viewer.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void setName(String name) {
        if (getServerPlayer() == null) {
            Main.get().sendDebugMessage("§cCannot set name on uninitialized NPC");
            return;
        }
        try {
            this.gameProfile = new GameProfile(gameProfile.getId(), name);
            Reflections.setField(getServerPlayer(), "gameProfile", this.gameProfile);
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.getWorld().equals(spawnLoc.getWorld()) && online.getLocation().distanceSquared(spawnLoc) < 100 * 100) {
                    ServerGamePacketListenerImpl connection = ((CraftPlayer) online).getHandle().connection;
                    ClientboundPlayerInfoUpdatePacket infoPacket = new ClientboundPlayerInfoUpdatePacket(
                            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
                            getServerPlayer()
                    );
                    connection.send(infoPacket);
                }
            }
            Main.get().sendDebugMessage("Updated NPC name to: " + name);
        } catch (Exception e) {
            Main.get().sendDebugMessage("§cFailed to set NPC name: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean isSneaking() {
        return sneaking;
    }

    @Override
    public void setPose(boolean sneaking) {
        if (getServerPlayer() == null) {
            Main.get().sendDebugMessage("§cCannot set pose on uninitialized NPC");
            return;
        }
        try {
            this.sneaking = sneaking;
            Pose nmsPose = sneaking ? Pose.CROUCHING : Pose.STANDING;
            getServerPlayer().setPose(nmsPose);
            SynchedEntityData data = getServerPlayer().getEntityData();
            data.set(EntityDataSerializers.POSE.createAccessor(6), nmsPose);
            updateMetadata();
        } catch (Exception e) {
            Main.get().sendDebugMessage("§cFailed to set NPC pose: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateMetadata() {
        if (getServerPlayer() == null) {
            Main.get().sendDebugMessage("§cCannot update metadata on uninitialized NPC");
            return;
        }
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getWorld().equals(spawnLoc.getWorld()) && online.getLocation().distanceSquared(spawnLoc) < 100 * 100) {
                updateMetadata(online);
            }
        }
    }

    private void updateMetadata(Player viewer) {
        if (getServerPlayer() == null) {
            Main.get().sendDebugMessage("§cCannot update metadata on uninitialized NPC");
            return;
        }
        try {
            ServerGamePacketListenerImpl connection = ((CraftPlayer) viewer).getHandle().connection;
            ClientboundSetEntityDataPacket dataPacket = new ClientboundSetEntityDataPacket(
                    getServerPlayer().getId(),
                    getServerPlayer().getEntityData().getNonDefaultValues()
            );
            connection.send(dataPacket);
        } catch (Exception e) {
            Main.get().sendDebugMessage("§cFailed to send metadata to " + viewer.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void destroy(List<CustomNPC> npcs) {
        if (getServerPlayer() == null) {
            Main.get().sendDebugMessage("§cCannot destroy uninitialized NPC");
            return;
        }
        try {
            Bukkit.getOnlinePlayers().forEach(this::hideNpcFromPlayer);
            npcs.remove(getServerPlayer());
            Main.get().sendDebugMessage("Destroyed NPC at " + spawnLoc);
        } catch (Exception e) {
            Main.get().sendDebugMessage("§cFailed to destroy NPC: " + e.getMessage());
                    e.printStackTrace();
        }
    }

    public void hideNpcFromPlayer(@NotNull Player player) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        connection.send(new ClientboundRemoveEntitiesPacket(getServerPlayer().getId()));

        connection.send(new ClientboundPlayerInfoRemovePacket(List.of(getServerPlayer().getUUID())));
    }
}