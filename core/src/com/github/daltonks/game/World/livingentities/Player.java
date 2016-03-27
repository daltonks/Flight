//Player's Paper Airplane Entity

package com.github.daltonks.game.World.livingentities;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;
import com.github.daltonks.engine.world.models.Models;
import com.github.daltonks.engine.world.physics.CollisionHandler;
import com.github.daltonks.game.World.physics.CollisionMasks;
import com.github.daltonks.game.datacompressor.LivingEntityInfo;
import com.github.daltonks.game.states.GameEngineState;

public class Player extends LivingEntity {
    private static LivingEntityInfo planeEntityInfo = LivingEntityInfos.getInfo(Models.get("plane"));

    public Player(EngineState engineState,
                  double x, double y, double z,
                  float qx, float qy, float qz, float qw) {
        super(engineState,
                x, y, z, qx, qy, qz, qw,
                Models.get("plane"),
                CollisionMasks.ALLY, new PlayerCollisionHandler());

        //engineState.getEngineWorld().getPhysicsWorld().addMaxVelocityBody(getModelComponent().getRigidBody(), 100);
    }

    @Override
    public void update(EngineState engineState, double delta) {
        super.update(engineState, delta);
        Vec3d vec = Pools.getVec3d();
        Vec3d throttle = GameEngineState.getThrottleNew();
        vec.set(throttle.x() * planeEntityInfo.forwardForceMax * delta, throttle.y() * planeEntityInfo.forwardForceMax * delta, 0);
        getModelComponent().getRigidBody().applyLocalForce(vec);
        Pools.recycle(vec);
        Pools.recycle(throttle);

        /*
        float[] forward = getTransformComponent().getForwardVector();
        Util.mult(forward, forward, .2f);
        engineState.getEngineWorld().addStaticEntity(
                new LinearProjectileEntity(
                        engineState,
                        "fourtriangleprimitive",
                        getTransformComponent().getLocation(),
                        forward[0], forward[1], forward[2],
                        ran(), ran(), ran(),
                        getModelComponent().getRigidBody().getPersonalCollisionMask(),
                        new ShotCollisionHandler(),
                        5
                )
        );*/
    }

    public boolean canTravelToPlanetoids() {
        return true;
    }

    private static class PlayerCollisionHandler implements CollisionHandler<Player> {
        @Override
        public void onCollision(Player entity, Entity collidedWithEntity) {

        }
    }
}