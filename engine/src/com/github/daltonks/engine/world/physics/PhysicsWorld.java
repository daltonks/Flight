package com.github.daltonks.engine.world.physics;

import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.InternalTickCallback;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;
import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;
import com.github.daltonks.engine.world.models.Model;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class PhysicsWorld {
    private static final int MAX_SUBSTEPS = 1;
    public static int numOfTicksProcessedLast = 0;

    private NoGravityDynamicsWorld physicsWorld;
    private ArrayList<BodyAndMaxVelocity> maxVelocityBodies = new ArrayList<>();

    public PhysicsWorld(float worldWidth) {
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        dispatcher.setNearCallback(new EngineNearCallback());

        float halfWorldWidth = worldWidth / 2 * Engine.ENGINE_TO_PHYSICS_WORLD_SCALE;

        Vector3f worldAabbMin = new Vector3f(-halfWorldWidth, -halfWorldWidth, -halfWorldWidth);
        Vector3f worldAabbMax = new Vector3f(halfWorldWidth, halfWorldWidth, halfWorldWidth);
        EngineAxisSweep3 overlappingPairCache = new EngineAxisSweep3(worldAabbMin, worldAabbMax);

        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

        physicsWorld = new NoGravityDynamicsWorld(
                dispatcher, overlappingPairCache, solver,
                collisionConfiguration);

        physicsWorld.setGravity(new Vector3f(0, 0, 0));

        physicsWorld.setInternalTickCallback(new InternalTickCallback() {
            public void internalTick(DynamicsWorld dynamicsWorld, float timeStep) {
                onSimulationTick(dynamicsWorld, timeStep);
            }
        }, null);
    }

    private void onSimulationTick(DynamicsWorld dynamicsWorld, float timeStep) {
        Dispatcher dispatcher = dynamicsWorld.getDispatcher();
        int manifoldCount = dispatcher.getNumManifolds();
        for(int i = 0; i < manifoldCount; i++) {
            PersistentManifold manifold = dispatcher.getManifoldByIndexInternal(i);
            EngineRigidBody body1 = (EngineRigidBody) manifold.getBody0();
            EngineRigidBody body2 = (EngineRigidBody) manifold.getBody1();
            for(int j = 0; j < manifold.getNumContacts(); j++) {
                ManifoldPoint contactPoint = manifold.getContactPoint(j);
                //contactPoint.appliedImpulse <- force of impact
                if(contactPoint.getDistance() < 0.0f) {
                    body1.onCollision(body2);
                    body2.onCollision(body1);
                    break;
                }
            }
        }

        Vector3f velocity = Pools.getVector3f();
        for(int i = 0; i < maxVelocityBodies.size(); i++) {
            BodyAndMaxVelocity bamv = maxVelocityBodies.get(i);
            bamv.body.getLinearVelocity(velocity);
            float speed = velocity.length();
            if(speed > bamv.maxVelocity) {
                velocity.normalize();
                velocity.scale(speed);
                bamv.body.setLinearVelocity(velocity);
            }
        }
        Pools.recycle(velocity);
    }

    private long lastTime = 0;
    public void stepSimulation() {
        long time = System.nanoTime();
        if(lastTime == 0) lastTime = time;
        double delta = (time - lastTime) / 1000000000.0;
        if(delta > .2) delta = .2;
        lastTime = time;
        numOfTicksProcessedLast = physicsWorld.stepSimulation((float) delta, MAX_SUBSTEPS);
    }

    public static ConvexHullShape createDynamicConvexShape(Model model, float scale) {
        scale *= Engine.ENGINE_TO_PHYSICS_WORLD_SCALE;

        ObjectArrayList<Vector3f> list = new ObjectArrayList<>();
        Model.NonDuplicatedVertices nonDuplicated = model.getNonDuplicatedVerticesAndIndices();
        for(int i = 0; i < nonDuplicated.vertices.size(); i++) {
            float[] loc = nonDuplicated.vertices.get(i);
            list.add(new Vector3f(loc[0] * scale, loc[1] * scale, loc[2] * scale));
        }
        ConvexHullShape shape = new ConvexHullShape(list);
        shape.calculateLocalInertia(model.getModelInfo().mass, new Vector3f(0, 0, 0));
        return shape;
    }

    public static BvhTriangleMeshShape createStaticShape(Model model, float scale) {
        scale *= Engine.ENGINE_TO_PHYSICS_WORLD_SCALE;
        Model.NonDuplicatedVertices nonDuplicated = model.getNonDuplicatedVerticesAndIndices();

        ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(nonDuplicated.vertices.size() * 3 * (Float.SIZE / 8));
        vertexBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer.clear();

        for(int i = 0; i < nonDuplicated.vertices.size(); i++) {
            float[] loc = nonDuplicated.vertices.get(i);
            vertexBuffer.putFloat(loc[0] * scale);
            vertexBuffer.putFloat(loc[1] * scale);
            vertexBuffer.putFloat(loc[2] * scale);
        }

        IndexedMesh indexedMesh = new IndexedMesh();

        indexedMesh.numTriangles = model.getNumberOfTriangles();
        indexedMesh.triangleIndexBase = nonDuplicated.indicesByteBuffer;
        indexedMesh.triangleIndexStride = 3 * (Integer.SIZE / 8);

        indexedMesh.numVertices = nonDuplicated.vertices.size();
        indexedMesh.vertexBase = vertexBuffer;
        indexedMesh.vertexStride = 3 * (Float.SIZE / 8);

        TriangleIndexVertexArray tiva = new TriangleIndexVertexArray();
        tiva.addIndexedMesh(indexedMesh, ScalarType.INTEGER);
        BvhTriangleMeshShape shape = new BvhTriangleMeshShape(tiva, true);
        if(model.getModelInfo().mass != 0) {
            shape.calculateLocalInertia(model.getModelInfo().mass, new Vector3f(0, 0, 0));
        }
        return shape;
    }

    private static Quat4f tempQuat = new Quat4f();
    public EngineRigidBody createAndAddRigidBody(
            Entity entity, CollisionShape shape, float mass,
            double x, double y, double z, float qx, float qy, float qz, float qw,
            byte personalCollisionMask, byte collidesWithMask,
            CollisionHandler collisionHandler, boolean disableDeactiviation) {

        Vector3f loc = Pools.getVector3f();
        x *= Engine.ENGINE_TO_PHYSICS_WORLD_SCALE;
        y *= Engine.ENGINE_TO_PHYSICS_WORLD_SCALE;
        z *= Engine.ENGINE_TO_PHYSICS_WORLD_SCALE;
        loc.set((float) x, (float) y, (float) z);

        Transform startTransform = Pools.getTransform();
        startTransform.setIdentity();
        startTransform.origin.set(loc);
        tempQuat.set(qx, qy, qz, qw);
        startTransform.setRotation(tempQuat);

        Vector3f localInertia = new Vector3f(0, 0, 0);
        if(mass != 0f) shape.calculateLocalInertia(mass, localInertia);

        DefaultMotionState motionState = new DefaultMotionState(startTransform);
        Pools.recycle(startTransform);
        Pools.recycle(loc);

        RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(
                mass, motionState, shape, localInertia);

        EngineRigidBody body = new EngineRigidBody(entity, rbInfo, personalCollisionMask, collidesWithMask, collisionHandler);
        body.setUserPointer(entity);
        body.setRestitution(0);

        physicsWorld.addRigidBody(body);
        if(disableDeactiviation) {
            body.forceActivationState(RigidBody.DISABLE_DEACTIVATION);
        }
        return body;
    }

    public void removeRigidBody(RigidBody body) {
        physicsWorld.removeRigidBody(body);
    }

    public void addMaxVelocityBody(EngineRigidBody body, float maxVelocity) {
        maxVelocityBodies.add(new BodyAndMaxVelocity(body, maxVelocity));
    }

    public static class BodyAndMaxVelocity {
        private float maxVelocity;
        private EngineRigidBody body;
        private BodyAndMaxVelocity(EngineRigidBody body, float maxVelocity) {
            this.maxVelocity = maxVelocity;
            this.body = body;
        }
    }
}