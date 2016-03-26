//Component in component-entity system

package com.github.daltonks.engine.world.entityComponent.components;

import com.github.daltonks.engine.util.interfaces.Updatable;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;

public abstract class Component implements Updatable {
    private Entity entity;
    public Component(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}