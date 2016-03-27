package com.github.daltonks.engine.world.camera;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;

public class ViewMatrix {
    private boolean dirty;
    private Vec3d location = new Vec3d();
    private Vec3d viewCenter = new Vec3d();
    private Vec3d up = new Vec3d();
    private Camera camera;
    private Matrix4 viewMatrix = new Matrix4();

    public ViewMatrix(Camera camera) {
        this.camera = camera;
        updateMatrix();
    }

    private void updateMatrix() {
        Vec3d toViewCent = viewCenter.clone().sub(location);
        Vector3 direction = Pools.getVector3();
        toViewCent.get(direction);
        Vector3 up = Pools.getVector3();
        this.up.get(up);
        viewMatrix.setToLookAt(direction, up);

        Pools.recycle(toViewCent);
        Pools.recycle(direction);
        Pools.recycle(up);

        camera.setDirty();
        dirty = false;
    }

    public Matrix4 getMatrix() {
        if(dirty)
            updateMatrix();
        return viewMatrix;
    }

    public void setLocation(Vec3d loc) {
        if(!loc.equals(location)) {
            location.set(loc);
            dirty = true;
        }
    }

    public Vec3d getLocation() {
        return location;
    }

    public void setViewCenter(Vec3d loc) {
        if(!loc.equals(viewCenter)) {
            viewCenter.set(loc);
            dirty = true;
        }
    }

    public Vec3d getViewCenter() {
        return viewCenter;
    }

    public void setUp(Vec3d loc) {
        if(!loc.equals(up)) {
            up.set(loc);
            dirty = true;
        }
    }

    public Vec3d getUp() {
        return up;
    }

    public boolean isDirty() {
        return dirty;
    }
}