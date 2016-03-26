package com.github.daltonks.game.World.livingentities;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Color;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.world.camera.Camera;
import com.github.daltonks.engine.world.entityComponent.components.modelComponents.ModelComponent;
import com.github.daltonks.engine.world.entityComponent.entities.base.ModelEntity;
import com.github.daltonks.engine.world.models.Model;
import com.github.daltonks.engine.world.physics.CollisionHandler;
import com.github.daltonks.game.World.items.base.Inventory;
import com.github.daltonks.game.World.physics.CollisionMasks;
import com.github.daltonks.game.datacompressor.LivingEntityInfo;

public class LivingEntity extends ModelEntity {
    private static final Color lightBlue = new Color(.38f, .91f, 1f);
    private static final Color green = new Color(.2f, .7f, .4f);
    private static final Color yellow = new Color(1, 1, .4f);
    private static final Color orange = new Color(1, .6f, .1f);
    private static final Color red = new Color(1, .2f, .25f);

    private Inventory inventory;
    private LivingEntityStats livingEntityStats;

    public LivingEntity(EngineState engineState,
                        double x, double y, double z,
                        float qx, float qy, float qz, float qw,
                        Model model,
                        byte team,
                        CollisionHandler collisionHandler) {

        super(engineState, x, y, z, qx, qy, qz, qw, -1, model, team, CollisionMasks.getEntityCollidesWithMask(team), true, collisionHandler, false);

        setStats(new LivingEntityStats(this));
        LivingEntityInfo info = LivingEntityInfos.getInfo(model);
        getModelComponent().getRigidBody().setDamping(info.linearDamping, info.angularDamping);
    }

    public void update(EngineState engineState, double delta) {
        super.update(engineState, delta);

        livingEntityStats.update(engineState, delta);
        if(inventory != null)
            inventory.update(engineState, delta);
    }

    public void drawOutline(Camera camera) {
        float healthPortion = (float) livingEntityStats.getHealth() / livingEntityStats.getMaxHealth();
        Color healthColor;
        if(healthPortion > .75) {
            healthColor = green.lerpClampNew(lightBlue, (healthPortion - .75f) * 4);
        } else if(healthPortion > .5) {
            healthColor = yellow.lerpClampNew(green, (healthPortion - .5f) * 4);
        } else if(healthPortion > .25) {
            healthColor = orange.lerpClampNew(yellow, (healthPortion - .25f) * 4);
        } else {
            healthColor = red.lerpClampNew(orange, healthPortion * 4);
        }

        ModelComponent mComp = getModelComponent();
        float prevScale = mComp.getScale();
        Color prevColor = Pools.getColor();
        prevColor.set(mComp.getColor());
        float outlineScale;
        if(this instanceof Player) {
            outlineScale = 1.1f;
        } else {
            outlineScale = 1.35f;
        }
        mComp.setScale(prevScale * outlineScale);
        mComp.setColor(healthColor);
        super.draw(camera);
        mComp.setScale(prevScale);
        mComp.setColor(prevColor);
        Pools.recycle(healthColor);
        Pools.recycle(prevColor);
    }

    public void setStats(LivingEntityStats livingEntityStats) {
        this.livingEntityStats = livingEntityStats;
    }

    public LivingEntityStats getStats() {
        return livingEntityStats;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return inventory;
    }
}