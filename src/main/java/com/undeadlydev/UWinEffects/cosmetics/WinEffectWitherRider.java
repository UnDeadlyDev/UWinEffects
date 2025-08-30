package com.undeadlydev.UWinEffects.cosmetics;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.interfaces.WinEffect;
import me.gamercoder215.mobchip.EntityBrain;
import me.gamercoder215.mobchip.ai.controller.NaturalMoveType;
import me.gamercoder215.mobchip.bukkit.BukkitBrain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class WinEffectWitherRider implements WinEffect, Cloneable {

    private static boolean loaded = false;
    private static double movementSpeed;
    private static float skullYield;
    private BukkitTask movementTask;
    private Wither wither;
    private EntityBrain brain;
    private Listener eventListener;

    @Override
    public void loadCustoms(Main plugin, String path) {
        if (!loaded) {
            movementSpeed = plugin.getWineffect().getDoubleOrDefault(path + ".movementSpeed", 2.0);
            skullYield = (float) plugin.getWineffect().getDoubleOrDefault(path + ".skullYield", 2.0);
            loaded = true;
        }
    }

    @Override
    public void start(Player p) {
        World world = p.getWorld();
        Location loc = p.getLocation().add(0, 1, 0);

        wither = world.spawn(loc, Wither.class);
        wither.setAI(false);
        wither.setInvulnerable(true);
        wither.setCustomName(p.getName() + "'s Wither");
        wither.setCustomNameVisible(true);
        wither.setGravity(false);
        try {
            Main.get().getCollisionAPI().setCollidable(wither, false);
        } catch (Exception e) {
            wither.setCollidable(false);
        }

        brain = BukkitBrain.getBrain(wither);
        brain.getBody().setHurtTime(20);
        wither.addPassenger(p);

        movementTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (p == null || !p.isOnline() || !world.getName().equals(p.getWorld().getName()) || wither.isDead() || !wither.getPassengers().contains(p)) {
                    Main.get().getCos().winEffectsTask.remove(p.getUniqueId()).stop();
                    return;
                }
                float yaw = p.getLocation().getYaw();
                brain.getBody().setPitch(p.getLocation().getPitch());
                brain.getBody().setYaw(yaw - 180);
                double angleInRadians = toRadians(-yaw);
                double x = sin(angleInRadians) * movementSpeed;
                double z = cos(angleInRadians) * movementSpeed;
                Vector v = wither.getLocation().getDirection();
                brain.getController().naturalMoveTo(x, v.getY() * 0.5, z, NaturalMoveType.SELF);
                if (wither.getLocation().getY() <= world.getMinHeight() - 15) {
                    Main.get().getCos().winEffectsTask.remove(p.getUniqueId()).stop();
                }
            }
        }.runTaskTimer(Main.get(), 0, 2);

        eventListener = new Listener() {
            @EventHandler
            public void onPlayerInteract(PlayerInteractEvent event) {
                if (event.getPlayer().equals(p) && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
                    if (wither != null && !wither.isDead() && wither.getPassengers().contains(p)) {
                        launchSkull(p);
                    }
                }
            }
            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                if (event.getPlayer().equals(p)) {
                    Main.get().getCos().winEffectsTask.remove(p.getUniqueId()).stop();
                }
            }

            @EventHandler
            public void onWitherDamage(EntityDamageEvent event) {
                if (event.getEntity().equals(wither)) {
                    event.setCancelled(true); // Prevent damage to Wither
                }
            }
            @EventHandler
            public void onVehicleExit(VehicleExitEvent event) {
                if (event.getVehicle().equals(wither) && event.getExited().equals(p)) {
                    Main.get().getCos().winEffectsTask.remove(p.getUniqueId()).stop();
                }
            }
        };
        Bukkit.getPluginManager().registerEvents(eventListener, Main.get());
    }

    private void launchSkull(Player p) {
        if (wither == null || wither.isDead()) return;
        Location skullLoc = wither.getLocation().add(0, wither.getEyeHeight(), 0);
        Vector direction = p.getLocation().getDirection().normalize();
        WitherSkull skull = wither.getWorld().spawn(skullLoc, WitherSkull.class);
        skull.setDirection(direction);
        skull.setVelocity(direction.multiply(1.5));
        skull.setYield(skullYield);
        skull.setShooter(wither);
    }

    @Override
    public void stop() {
        if (movementTask != null) {
            movementTask.cancel();
            movementTask = null;
        }
        if (wither != null && !wither.isDead()) {
            wither.remove();
            wither = null;
        }
        if (eventListener != null) {
            HandlerList.unregisterAll(eventListener);
            eventListener = null;
        }
        brain = null; // Clear brain reference
    }

    @Override
    public WinEffect clone() {
        return new WinEffectWitherRider();
    }
}