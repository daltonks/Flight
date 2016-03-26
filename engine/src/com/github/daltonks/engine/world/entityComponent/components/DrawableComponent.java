package com.github.daltonks.engine.world.entityComponent.components;

import com.github.daltonks.engine.util.interfaces.Drawable;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;

public abstract class DrawableComponent extends Component implements Drawable {

    public DrawableComponent(Entity entity) {
        super(entity);
    }

}