package com.github.daltonks.engine.world.camera;

import android.opengl.Matrix;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;

public class ViewMatrix {
    private boolean dirty, transposeDirty;
    private Vec3d location = new Vec3d();
    private Vec3d viewCenter = new Vec3d();
    private Vec3d up = new Vec3d();
    private Camera camera;
    private float[] viewMatrix = new float[16];
    private float[] transpose = new float[16];

    public ViewMatrix(Camera camera) {
        this.camera = camera;
        updateMatrix();
    }

    private void updateMatrix() {
        Vec3d toViewCent = viewCenter.clone().sub(location);
        Matrix.setLookAtM(viewMatrix, 0,
                0, 0, 0,
                toViewCent.xf(), toViewCent.yf(), toViewCent.zf(),
                up.xf(), up.yf(), up.zf()
        );
        Pools.recycle(toViewCent);

        camera.setDirty();
        transposeDirty = true;
        dirty = false;
    }

    public float[] getMatrix() {
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

    public float[] getTranspose() {
        if(transposeDirty) {
            if(dirty)
                updateMatrix();

            Matrix.transposeM(transpose, 0, viewMatrix, 0);
            transposeDirty = false;
        }
        return transpose;
    }

    public boolean isDirty() {
        return dirty;
    }
}