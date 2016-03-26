//Entity just for UI objects
//Class handles the separate view matrix

package com.github.daltonks.engine.world.ui;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.entityComponent.components.modelComponents.ModelComponent;
import com.github.daltonks.engine.world.entityComponent.components.transformComponents.TransformComponent;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;
import com.github.daltonks.engine.world.models.Model;

public class UIEntity extends Entity {
    private UIBoundsComponent uiBoundsComponent;
    private boolean glued = false;
    private byte glueXMod, glueYMod;
    private float glueWorldXOffset, glueWorldYOffset;

    public UIEntity(EngineState engineState, Model model) {
        this(engineState, model, 0, 0, 0);
    }

    public UIEntity(EngineState engineState, Model model, double x, double y, double z) {
        super(engineState);
        setTransformComponent(new TransformComponent(this, x, y, z));
        if(model != null) {
            setDrawableComponent(new ModelComponent(this, model));
        }
    }

    @Override
    public void update(EngineState engineState, double delta) {
        if(uiBoundsComponent != null) uiBoundsComponent.update(engineState, delta);
    }

    public void glueTo(Glue where, float worldOffsetX, float worldOffsetY) {
        glued = true;
        glueXMod = where.xModifier;
        glueYMod = where.yModifier;
        glueWorldXOffset = worldOffsetX;
        glueWorldYOffset = worldOffsetY;
    }

    public void glueTo(Glue where1, Glue where2, float worldOffsetX, float worldOffsetY) {
        glued = true;
        glueXMod = (byte) (where1.xModifier + where2.xModifier);
        glueYMod = (byte) (where1.yModifier + where2.yModifier);
        glueWorldXOffset = worldOffsetX;
        glueWorldYOffset = worldOffsetY;
    }

    public void onSurfaceChanged(EngineState engineState, int screenWidth, int screenHeight) {
        if(glued) {
            updateGlue(engineState);
        }
    }

    public void updateGlue(EngineState engineState) {
        float width, height;
        if(uiBoundsComponent == null) {
            width = getModelComponent().getXLength();
            height = getModelComponent().getYLength();
        } else {
            width = uiBoundsComponent.getWidth();
            height = uiBoundsComponent.getHeight();
        }

        Vec3d worldLoc = engineState.getUICamera().screenToWorldZPlaneNew(glueXMod, glueYMod);
        getTransformComponent().setLocation(
                worldLoc.x() - glueXMod * width / 2 + glueWorldXOffset,
                worldLoc.y() - glueYMod * height / 2 + glueWorldYOffset,
                0);

        Pools.recycle(worldLoc);
    }

    public UIBoundsComponent getUIBoundsComponent() {
        return uiBoundsComponent;
    }

    public void setUIBoundsComponent(UIBoundsComponent component) {
        this.uiBoundsComponent = component;
    }

    public enum Glue {
        UP(0, 1), DOWN(0, -1), LEFT(-1, 0), RIGHT(1, 0);

        private byte xModifier, yModifier;

        Glue(int xModifier, int yModifier) {
            this.xModifier = (byte) xModifier;
            this.yModifier = (byte) yModifier;
        }
    }
}