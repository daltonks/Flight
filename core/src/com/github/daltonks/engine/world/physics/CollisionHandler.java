package com.github.daltonks.engine.world.physics;

import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;

public interface CollisionHandler<T extends Entity> {
    void onCollision(T entity, Entity collidedWithEntity);
}