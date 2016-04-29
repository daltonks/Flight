package com.github.daltonks.game.World.entities.livingentities;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.util.interfaces.Positionable;
import com.github.daltonks.engine.world.entityComponent.components.Component;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;
import com.github.daltonks.engine.world.physics.EngineRigidBody;
import com.github.daltonks.game.World.engineworlds.GameEngineWorld;
import com.github.daltonks.game.datacompressor.LivingEntityInfo;

public class FlyingAIComponent extends Component {
    private LivingEntityInfo livingEntityInfo;
    private Positionable moveTo;
    private Vec3d moveToSingleLocation = new Vec3d(0, 0, 0);
    private Positionable aimTo;
    private Vec3d aimToSingleLocation = new Vec3d(0, 0, 0);

    public FlyingAIComponent(Entity entity) {
        super(entity);
        this.livingEntityInfo = LivingEntityInfos.getInfo(entity.getModelComponent().getModel());
        setMoveTo(getPlayer().getTransformComponent(), true);
        setAimTo(getPlayer().getTransformComponent(), true);
        livingEntityInfo.lowerSpeedRangeSquared =
                livingEntityInfo.lowerSpeedRange * livingEntityInfo.lowerSpeedRange;
    }

    @Override
    public void update(EngineState engineState, double delta) {
        EngineRigidBody body = getEntity().getModelComponent().getRigidBody();
        //Aim
        body.rotateToward(aimTo.getLocation(), livingEntityInfo.rotVelocityMax);

        //Move
        Vec3d location = getEntity().getTransformComponent().getLocation();
        double distanceSquared = location.distanceSquaredTo(moveTo.getLocation());
        float forceMult = 1;
        if(distanceSquared < livingEntityInfo.lowerSpeedRangeSquared)
            forceMult = (float) (Math.sqrt(distanceSquared) / livingEntityInfo.lowerSpeedRange);
        Vec3d throttle = moveTo.getLocation().clone().sub(location);
        throttle.setLength(livingEntityInfo.forwardForceMax * forceMult * (float) delta);
        body.applyForce(throttle);
        Pools.recycle(throttle);
    }

    private void setMoveTo(Positionable moveTo, boolean useFutureLocations) {
        if(useFutureLocations) {
            this.moveTo = moveTo;
        } else {
            moveToSingleLocation.set(moveTo.getLocation());
            this.moveTo = moveToSingleLocation;
        }
    }

    private void setAimTo(Positionable aimTo, boolean useFutureLocations) {
        if(useFutureLocations) {
            this.aimTo = aimTo;
        } else {
            aimToSingleLocation.set(aimTo.getLocation());
            this.aimTo = aimToSingleLocation;
        }
    }

    private Player getPlayer() {
        return GameEngineWorld.INSTANCE.getPlayer();
    }
}