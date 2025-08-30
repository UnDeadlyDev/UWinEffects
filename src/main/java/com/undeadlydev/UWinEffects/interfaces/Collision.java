package com.undeadlydev.UWinEffects.interfaces;

import org.bukkit.entity.Entity;

public interface  Collision {

    void setCollidable(Entity entity, boolean collidable);

    boolean isCollidable(Entity entity);

}
