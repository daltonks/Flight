//Simple width-height component for UIEntities

package com.github.daltonks.engine.world.ui;

import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.entityComponent.components.Component;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;

public class UIBoundsComponent extends Component {
    private boolean hovering = false;
    private float width, height;

    public UIBoundsComponent(Entity entity) {
        this(entity, 1, 1);
    }

    public UIBoundsComponent(Entity entity, float xScale, float yScale) {
        this(entity, entity.getModelComponent().getXLength(), entity.getModelComponent().getYLength(), xScale, yScale);
    }

    public UIBoundsComponent(Entity entity, float width, float height, float xScale, float yScale) {
        super(entity);
        this.width = width * xScale;
        this.height = height * yScale;
    }

    @Override
    public void update(EngineState engineState, double delta) {

    }

    public boolean onCollidedDown(EngineState engineState, float openGLX, float openGLY) {
        onHover(engineState, openGLX, openGLY);
        return true;
    }

    public boolean onCollidedSwipe(EngineState engineState, float dx, float dy, float openGLX, float openGLY) {
        if(!hovering) {
            onHover(engineState, openGLX, openGLY);
            hovering = true;
        }
        return true;
    }

    public boolean onNonCollidedSwipe(EngineState engineState, float dx, float dy, float openGLX, float openGLY) {
        if(hovering) {
            onHoverStopped(engineState, openGLX, openGLY);
            hovering = false;
        }
        return true;
    }

    public boolean onCollidedUp(EngineState engineState, float openGLX, float openGLY) {
        if(hovering) {
            onHoverStopped(engineState, openGLX, openGLY);
            hovering = false;
        }
        return true;
    }

    public boolean onNonCollidedUp(EngineState engineState, float openGLX, float openGLY) {
        if(hovering) {
            onHoverStopped(engineState, openGLX, openGLY);
            hovering = false;
        }
        return true;
    }

    public void onHover(EngineState engineState, float openGLX, float openGLY) {

    }

    public void onHoverStopped(EngineState engineState, float openGLX, float openGLY) {

    }

    public boolean isInBounds(Vec3d worldCoordinates) {
        float hWidth = width / 2;
        float hHeight = height / 2;
        Vec3d loc = getEntity().getTransformComponent().getLocation();
        return worldCoordinates.x() >= loc.x() - hWidth && worldCoordinates.x() <= loc.x() + hWidth
                && worldCoordinates.y() >= loc.y() - hHeight && worldCoordinates.y() <= loc.y() + hHeight;
    }

    public boolean isInBounds(float openGLX, float openGLY) {
        Vec3d loc = Engine.INSTANCE.getCurrentSubActivity().getUICamera().screenToWorldZPlaneNew(openGLX, openGLY);
        boolean inBounds = isInBounds(loc);
        Pools.recycle(loc);
        return inBounds;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getWidth() {
        return width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getHeight() {
        return height;
    }
}