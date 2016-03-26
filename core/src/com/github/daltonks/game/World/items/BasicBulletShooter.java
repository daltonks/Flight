package com.github.daltonks.game.World.items;

import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.entityComponent.entities.LinearProjectileEntity;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;
import com.github.daltonks.engine.world.models.Models;
import com.github.daltonks.engine.world.physics.CollisionHandler;
import com.github.daltonks.game.World.events.ChangeHealthEvent;
import com.github.daltonks.game.World.items.base.Item;
import com.github.daltonks.game.World.items.base.itemstacks.ItemStack;
import com.github.daltonks.game.World.items.base.itemstacks.ToggleItemStack;
import com.github.daltonks.game.World.livingentities.LivingEntity;

public class BasicBulletShooter extends Item {
    private static final float SPEED = .2f;
    private static final double TIME_BETWEEN_SHOTS = .4;
    private static final double AGE_OF_DEATH = 5;
    private static final int DAMAGE = 1;

    public BasicBulletShooter(float boundingSphereRadius, int id) {
        super(ActivationType.TOGGLE, "basicbullet", boundingSphereRadius, id);
    }

    public ItemStack newItemStack(LivingEntity entity, int amount) {
        return new BBSItemStack(entity, this);
    }

    private static class BBSItemStack extends ToggleItemStack {
        boolean activated = false;

        public BBSItemStack(LivingEntity entity, Item item) {
            super(entity, item);
        }

        @Override
        protected void onToggled(boolean toggleOn) {
            activated = toggleOn;
        }

        double accum;
        public void update(EngineState engineState, double delta) {
            super.update(engineState, delta);
            if(!activated)
                return;
            accum += delta;
            while(accum >= TIME_BETWEEN_SHOTS) {
                Vec3d forward = getEntity().getTransformComponent().getForwardNew();
                forward.mult(SPEED);
                Vec3d torque = Pools.getVec3d().set(0, 0, 0);
                BBSCollisionHandler bbsch = new BBSCollisionHandler();
                LinearProjectileEntity projectile = new LinearProjectileEntity(
                        engineState,
                        Models.get("basicbullet"),
                        getEntity().getTransformComponent().getLocation(),
                        forward,
                        torque,
                        getEntity().getModelComponent().getRigidBody().getPersonalCollisionMask(),
                        bbsch,
                        AGE_OF_DEATH
                );
                bbsch.projectile = projectile;
                engineState.getEngineWorld().addEntity(projectile);
                Pools.recycle(forward);
                Pools.recycle(torque);
                accum -= TIME_BETWEEN_SHOTS;
            }
        }
    }

    private static class BBSCollisionHandler implements CollisionHandler {
        boolean alreadyCollided = false;
        LinearProjectileEntity projectile;
        @Override
        public void onCollision(final Entity entity, final Entity collidedWithEntity) {
            if(collidedWithEntity instanceof LivingEntity && !alreadyCollided) {
                Engine.INSTANCE.getCurrentSubActivity().getEngineWorld().addAfterPhysicsStepRunnable(new Runnable() {
                    @Override
                    public void run() {
                        ChangeHealthEvent changeHealth = new ChangeHealthEvent(entity, (LivingEntity) collidedWithEntity, -DAMAGE);
                        Engine.INSTANCE.getCurrentSubActivity().getEventSystemContainer().performEvent(changeHealth);
                        Engine.INSTANCE.getCurrentSubActivity().getEngineWorld().removeEntity(projectile);
                    }
                });
                alreadyCollided = true;
            }
        }
    }
}