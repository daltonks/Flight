//my implementation of Bullet's NearCallback to handle my physics masks

package com.github.daltonks.engine.world.physics;

import com.bulletphysics.collision.broadphase.BroadphasePair;
import com.bulletphysics.collision.broadphase.DispatchFunc;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.ManifoldResult;
import com.bulletphysics.collision.dispatch.NearCallback;

public class EngineNearCallback extends NearCallback {
    private final ManifoldResult contactPointResult = new ManifoldResult();

    @Override
    public void handleCollision(BroadphasePair collisionPair, CollisionDispatcher dispatcher, DispatcherInfo dispatchInfo) {
        CollisionObject colObj1 = (CollisionObject) collisionPair.pProxy0.clientObject;
        CollisionObject colObj2 = (CollisionObject) collisionPair.pProxy1.clientObject;
        EngineRigidBody body1 = (EngineRigidBody) colObj1;
        EngineRigidBody body2 = (EngineRigidBody) colObj2;

        if(((body1.getCollidesWithMask() & body2.getPersonalCollisionMask()) == 0)
            //|| ((body1.getPersonalCollisionMask() & body2.getCollidesWithMask()) == 0b0)
                ) {
            return;
        } else if(!body1.isActive() && !body2.isActive()) {
            return;
        } else if(!body1.checkCollideWith(body2)) {
            return;
        }

        if(collisionPair.algorithm == null) {
            collisionPair.algorithm = dispatcher.findAlgorithm(colObj1, colObj2);
        }

        contactPointResult.init(colObj1, colObj2);
        if(dispatchInfo.dispatchFunc == DispatchFunc.DISPATCH_DISCRETE) {
            collisionPair.algorithm.processCollision(colObj1, colObj2, dispatchInfo, this.contactPointResult);
        } else {
            float toi = collisionPair.algorithm.calculateTimeOfImpact(colObj1, colObj2, dispatchInfo, this.contactPointResult);
            if(dispatchInfo.timeOfImpact > toi) {
                dispatchInfo.timeOfImpact = toi;
            }
        }
    }
}