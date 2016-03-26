//Holds the raw Model object, rigid body, and scale values for model rendering

package com.github.daltonks.engine.world.entityComponent.components.modelComponents;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Color;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.camera.Camera;
import com.github.daltonks.engine.world.entityComponent.components.DrawableComponent;
import com.github.daltonks.engine.world.entityComponent.components.transformComponents.TransformComponent;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;
import com.github.daltonks.engine.world.models.Model;
import com.github.daltonks.engine.world.models.Models;
import com.github.daltonks.engine.world.physics.CollisionHandler;
import com.github.daltonks.engine.world.physics.EngineRigidBody;
import com.github.daltonks.engine.world.physics.ModelPhysicsShapes;

public class ModelComponent extends DrawableComponent {
    private boolean frustumCulling = true;
    protected float scale = 1;
    private Model model;
    private EngineRigidBody rigidBody;
    private Color color = new Color(1, 1, 1);

    public ModelComponent(Entity entity, Model model) {
        this(entity, model, model.getModelInfo().defaultScale);
    }

    public ModelComponent(Entity entity, Model model, float scale) {
        super(entity);
        this.model = model;
        this.scale = scale;
    }

    public void draw(Camera camera) {
        TransformComponent transformComponent = getEntity().getTransformComponent();
        Vec3d loc = transformComponent.getLocation();
        if(!frustumCulling || camera.getFrustumCuller().isSphereInFrustum(loc, getRadius())) {
            model.draw(
                    loc,
                    transformComponent.getRotationMatrix(),
                    scale, scale, scale,
                    color,
                    camera);
        }
    }

    public EngineRigidBody getRigidBody() {
        return rigidBody;
    }

    public void createRigidBody(EngineState engineState, double x, double y, double z, float qx, float qy, float qz, float qw,
                                byte personalCollisionMask, byte collidesWithMask, CollisionHandler collisionHandler, boolean disableDeactiviation) {
        this.rigidBody = engineState.getEngineWorld().getPhysicsWorld().createAndAddRigidBody(
                getEntity(),
                ModelPhysicsShapes.getPhysicsShape(getModel(), scale),
                getModel().getModelInfo().mass,
                x, y, z,
                qx, qy, qz, qw,
                personalCollisionMask,
                collidesWithMask,
                collisionHandler,
                disableDeactiviation
        );
    }

    public Model getModel() {
        return model;
    }

    public void setModel(String modelName) {
        this.model = Models.get(modelName);
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setColor(float r, float g, float b) {
        color.set(r, g, b);
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public Color getColor() {
        return color;
    }

    public void setFrustumCulling(boolean culling) {
        this.frustumCulling = culling;
    }

    public float getScale() {
        return scale;
    }

    public float getRadius() {
        return getModel().getModelInfo().radius * scale;
    }

    public float getXLength() {
        return model.getXLength() * getScale();
    }

    public float getYLength() {
        return model.getYLength() * getScale();
    }

    public float getZLength() {
        return model.getZLength() * getScale();
    }

    @Override
    public void update(EngineState engineState, double delta) {

    }
}