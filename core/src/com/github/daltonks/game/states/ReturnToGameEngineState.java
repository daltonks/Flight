package com.github.daltonks.game.states;

import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;
import com.github.daltonks.game.World.engineworlds.GameEngineWorld;
import com.github.daltonks.game.World.entities.livingentities.Player;

import javax.vecmath.Vector3f;

public abstract class ReturnToGameEngineState extends EngineState {
    public boolean returnToGame() {
        final Player player = GameEngineWorld.INSTANCE.getPlayer();

        Entity planetoid = GameEngineWorld.INSTANCE.getPlanetoidTraveledToLast();
        if(planetoid != null) {
            Vec3d toPlayer = player.getTransformComponent().getLocation().clone().sub(planetoid.getTransformComponent().getLocation());
            toPlayer.normalize();
            float distanceToPlacePlayer = planetoid.getModelComponent().getRadius();
            distanceToPlacePlayer += 300;
            toPlayer.mult(distanceToPlacePlayer);
            toPlayer.add(planetoid.getTransformComponent().getLocation());
            player.getTransformComponent().setLocation(toPlayer);
            Pools.recycle(toPlayer);

            GameEngineWorld.INSTANCE.setPlanetoidTraveledToLast(null);

            Engine.INSTANCE.getCurrentSubActivity().getEngineWorld().addAfterPhysicsStepRunnable(new Runnable() {
                @Override
                public void run() {
                    Vector3f vec = Pools.getVector3f();
                    vec.set(0, 0, 0);
                    player.getModelComponent().getRigidBody().clearForces();
                    player.getModelComponent().getRigidBody().setAngularVelocity(vec);
                    player.getModelComponent().getRigidBody().setLinearVelocity(vec);
                    Pools.recycle(vec);
                }
            });
        }

        Engine.INSTANCE.setCurrentSubActivity("game", new GameTransition());
        return true;
    }
}