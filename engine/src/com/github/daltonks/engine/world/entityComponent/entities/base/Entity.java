//Entities in this component-entity system only contain an id and Components
//hash function returns the id for easy use in HashMaps and HashSets

package com.github.daltonks.engine.world.entityComponent.entities.base;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.interfaces.Drawable;
import com.github.daltonks.engine.util.interfaces.Updatable;
import com.github.daltonks.engine.world.camera.Camera;
import com.github.daltonks.engine.world.entityComponent.components.DrawableComponent;
import com.github.daltonks.engine.world.entityComponent.components.modelComponents.ModelComponent;
import com.github.daltonks.engine.world.entityComponent.components.transformComponents.TransformComponent;

public class Entity implements Updatable, Drawable, Comparable<Entity> {
    private int id;
    private TransformComponent transformComponent;
    private DrawableComponent drawableComponent;

    public Entity(int id) {
        this.id = id;
    }

    public Entity(EngineState engineState) {
        if(engineState != null) {
            id = engineState.generateEntityID();
        }
    }

    public void update(EngineState engineState, double delta) {
        if(transformComponent != null) transformComponent.update(engineState, delta);
        if(drawableComponent != null) drawableComponent.update(engineState, delta);
    }

    public void draw(Camera camera) {
        if(drawableComponent != null)
            drawableComponent.draw(camera);
    }

    public void setTransformComponent(TransformComponent transformComponent) {
        this.transformComponent = transformComponent;
    }

    public void setDrawableComponent(DrawableComponent drawableComponent) {
        this.drawableComponent = drawableComponent;
    }

    public TransformComponent getTransformComponent() {
        return transformComponent;
    }

    public ModelComponent getModelComponent() {
        return (ModelComponent) drawableComponent;
    }

    public DrawableComponent getDrawableComponent() {
        return drawableComponent;
    }

    public int getID() {
        return id;
    }

    public void onDestroy(EngineState engineState) {
        engineState.recycleEntityID(getID());
        if(getModelComponent() != null && getModelComponent().getRigidBody() != null) {
            engineState.getEngineWorld().getPhysicsWorld().removeRigidBody(getModelComponent().getRigidBody());
        }
    }

    @Override
    public int compareTo(Entity other) {
        if(id < other.id) {
            return -1;
        } else if(id == other.id) {
            return 0;
        } else {
            return 1;
        }
    }
}