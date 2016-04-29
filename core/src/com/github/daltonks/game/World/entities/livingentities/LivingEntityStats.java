package com.github.daltonks.game.World.entities.livingentities;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Color;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.camera.Camera;
import com.github.daltonks.engine.world.entityComponent.components.Component;
import com.github.daltonks.engine.world.entityComponent.components.transformComponents.TransformComponent;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;
import com.github.daltonks.engine.world.models.Model;
import com.github.daltonks.engine.world.models.Models;
import com.github.daltonks.game.datacompressor.LivingEntityInfo;

public class LivingEntityStats extends Component {
    private static final Model healthOrbModel = Models.get("healthorb");
    private static final Color green = new Color(.2f, .7f, .4f);
    private static final Color yellow = new Color(1, 1, .4f);
    private static final Color orange = new Color(1, .6f, .1f);
    private static final Color red = new Color(1, .2f, .25f);

    private int health, maxHealth;

    public LivingEntityStats(Entity entity) {
        super(entity);
        LivingEntityInfo info = LivingEntityInfos.getInfo(entity.getModelComponent().getModel());
        this.health = this.maxHealth = info.maxHealth;
    }

    @Override
    public void update(EngineState engineState, double delta) {

    }

    public void drawHealth(Camera camera) {
        if(health <= 0)
            return;

        TransformComponent transform = getEntity().getTransformComponent();
        Vec3d loc = transform.getLocation();
        Vec3d up = camera.getViewMatrix().getUp().clone();
        float scale = 1;
        if(!(getEntity() instanceof Player)) {
            scale *= transform.getLocation().distanceTo(camera.getViewMatrix().getLocation()) * .015;
        }
        up.mult(getEntity().getModelComponent().getRadius() + healthOrbModel.getModelInfo().radius * scale);
        up.add(loc);
        if(camera.getFrustumCuller().isSphereInFrustum(up, scale * healthOrbModel.getModelInfo().radius)) {
            float healthPortion = (float) health / maxHealth;
            Color color;
            if(healthPortion > 2.0 / 3.0) {
                color = yellow.lerpClampNew(green, (healthPortion - .75f) * 4);
            } else if(healthPortion > 1.0 / 3.0) {
                color = orange.lerpClampNew(yellow, (healthPortion - .5f) * 4);
            } else {
                color = red.lerpClampNew(orange, healthPortion * 3);
            }
            healthOrbModel.draw(up, transform.getRotationMatrix(), scale, scale, scale, color, camera);
            Pools.recycle(color);
        }
        Pools.recycle(up);
    }

    public void setHealthFromEvent(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }
}