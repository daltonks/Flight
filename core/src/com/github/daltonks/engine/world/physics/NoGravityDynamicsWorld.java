package com.github.daltonks.engine.world.physics;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.BulletStats;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.linearmath.CProfileManager;
import com.bulletphysics.linearmath.ScalarUtil;

public class NoGravityDynamicsWorld extends DiscreteDynamicsWorld {

    public NoGravityDynamicsWorld(Dispatcher dispatcher, BroadphaseInterface pairCache, ConstraintSolver constraintSolver, CollisionConfiguration collisionConfiguration) {
        super(dispatcher, pairCache, constraintSolver, collisionConfiguration);
    }

    public int stepSimulation(float timeStep, int maxSubSteps, float fixedTimeStep) {
        this.startProfiling(timeStep);
        long t0 = System.nanoTime();
        BulletStats.pushProfile("stepSimulation");

        int clampedSimulationSteps;
        try {
            int numSimulationSubSteps = 0;
            if(maxSubSteps != 0) {
                this.localTime += timeStep;
                if(this.localTime >= fixedTimeStep) {
                    numSimulationSubSteps = (int)(this.localTime / fixedTimeStep);
                    this.localTime -= (float)numSimulationSubSteps * fixedTimeStep;
                }
            } else {
                fixedTimeStep = timeStep;
                this.localTime = timeStep;
                if(ScalarUtil.fuzzyZero(timeStep)) {
                    numSimulationSubSteps = 0;
                    maxSubSteps = 0;
                } else {
                    numSimulationSubSteps = 1;
                    maxSubSteps = 1;
                }
            }

            if(this.getDebugDrawer() != null) {
                BulletGlobals.setDeactivationDisabled((this.getDebugDrawer().getDebugMode() & 16) != 0);
            }

            if(numSimulationSubSteps != 0) {
                this.saveKinematicState(fixedTimeStep);
                clampedSimulationSteps = numSimulationSubSteps > maxSubSteps?maxSubSteps:numSimulationSubSteps;

                for(int i = 0; i < clampedSimulationSteps; ++i) {
                    this.internalSingleStepSimulation(fixedTimeStep);
                    this.synchronizeMotionStates();
                }
            }

            this.synchronizeMotionStates();
            this.clearForces();
            CProfileManager.incrementFrameCounter();
            clampedSimulationSteps = numSimulationSubSteps;
        } finally {
            BulletStats.popProfile();
            BulletStats.stepSimulationTime = (System.nanoTime() - t0) / 1000000L;
        }

        return clampedSimulationSteps;
    }
}