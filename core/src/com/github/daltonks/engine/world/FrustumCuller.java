//Typical frustum culling to avoid attempting to draw models outside of the camera-view bounds

package com.github.daltonks.engine.world;

import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.camera.Camera;

public class FrustumCuller {
    private static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static final int BOTTOM = 2;
    private static final int TOP = 3;
    private static final int BACK = 4;
    private static final int FRONT = 5;

    private static final int A = 0;
    private static final int B = 1;
    private static final int C = 2;
    private static final int D = 3;

    private Camera camera;
    private float[][] frustum = new float[6][4];

    public FrustumCuller(Camera camera) {
        this.camera = camera;
    }

    public void updateFrustum() {
        float[] clipMatrix = Pools.getFloat16();
        float[] projectionMat = camera.getProjectionMatrix().getMatrix().val;
        float[] viewMat = camera.getViewMatrix().getMatrix().val;

        clipMatrix[0] = viewMat[0] * projectionMat[0] + viewMat[1] * projectionMat[4] + viewMat[2] * projectionMat[8] + viewMat[3] * projectionMat[12];
        clipMatrix[1] = viewMat[0] * projectionMat[1] + viewMat[1] * projectionMat[5] + viewMat[2] * projectionMat[9] + viewMat[3] * projectionMat[13];
        clipMatrix[2] = viewMat[0] * projectionMat[2] + viewMat[1] * projectionMat[6] + viewMat[2] * projectionMat[10] + viewMat[3] * projectionMat[14];
        clipMatrix[3] = viewMat[0] * projectionMat[3] + viewMat[1] * projectionMat[7] + viewMat[2] * projectionMat[11] + viewMat[3] * projectionMat[15];

        clipMatrix[4] = viewMat[4] * projectionMat[0] + viewMat[5] * projectionMat[4] + viewMat[6] * projectionMat[8] + viewMat[7] * projectionMat[12];
        clipMatrix[5] = viewMat[4] * projectionMat[1] + viewMat[5] * projectionMat[5] + viewMat[6] * projectionMat[9] + viewMat[7] * projectionMat[13];
        clipMatrix[6] = viewMat[4] * projectionMat[2] + viewMat[5] * projectionMat[6] + viewMat[6] * projectionMat[10] + viewMat[7] * projectionMat[14];
        clipMatrix[7] = viewMat[4] * projectionMat[3] + viewMat[5] * projectionMat[7] + viewMat[6] * projectionMat[11] + viewMat[7] * projectionMat[15];

        clipMatrix[8] = viewMat[8] * projectionMat[0] + viewMat[9] * projectionMat[4] + viewMat[10] * projectionMat[8] + viewMat[11] * projectionMat[12];
        clipMatrix[9] = viewMat[8] * projectionMat[1] + viewMat[9] * projectionMat[5] + viewMat[10] * projectionMat[9] + viewMat[11] * projectionMat[13];
        clipMatrix[10] = viewMat[8] * projectionMat[2] + viewMat[9] * projectionMat[6] + viewMat[10] * projectionMat[10] + viewMat[11] * projectionMat[14];
        clipMatrix[11] = viewMat[8] * projectionMat[3] + viewMat[9] * projectionMat[7] + viewMat[10] * projectionMat[11] + viewMat[11] * projectionMat[15];

        clipMatrix[12] = viewMat[12] * projectionMat[0] + viewMat[13] * projectionMat[4] + viewMat[14] * projectionMat[8] + viewMat[15] * projectionMat[12];
        clipMatrix[13] = viewMat[12] * projectionMat[1] + viewMat[13] * projectionMat[5] + viewMat[14] * projectionMat[9] + viewMat[15] * projectionMat[13];
        clipMatrix[14] = viewMat[12] * projectionMat[2] + viewMat[13] * projectionMat[6] + viewMat[14] * projectionMat[10] + viewMat[15] * projectionMat[14];
        clipMatrix[15] = viewMat[12] * projectionMat[3] + viewMat[13] * projectionMat[7] + viewMat[14] * projectionMat[11] + viewMat[15] * projectionMat[15];

        frustum[LEFT][A] = clipMatrix[3] + clipMatrix[0];
        frustum[LEFT][B] = clipMatrix[7] + clipMatrix[4];
        frustum[LEFT][C] = clipMatrix[11] + clipMatrix[8];
        frustum[LEFT][D] = clipMatrix[15] + clipMatrix[12];
        normalizePlane(LEFT);

        frustum[RIGHT][A] = clipMatrix[3] - clipMatrix[0];
        frustum[RIGHT][B] = clipMatrix[7] - clipMatrix[4];
        frustum[RIGHT][C] = clipMatrix[11] - clipMatrix[8];
        frustum[RIGHT][D] = clipMatrix[15] - clipMatrix[12];
        normalizePlane(RIGHT);

        frustum[BOTTOM][A] = clipMatrix[3] + clipMatrix[1];
        frustum[BOTTOM][B] = clipMatrix[7] + clipMatrix[5];
        frustum[BOTTOM][C] = clipMatrix[11] + clipMatrix[9];
        frustum[BOTTOM][D] = clipMatrix[15] + clipMatrix[13];
        normalizePlane(BOTTOM);

        frustum[TOP][A] = clipMatrix[3] - clipMatrix[1];
        frustum[TOP][B] = clipMatrix[7] - clipMatrix[5];
        frustum[TOP][C] = clipMatrix[11] - clipMatrix[9];
        frustum[TOP][D] = clipMatrix[15] - clipMatrix[13];
        normalizePlane(TOP);

        frustum[FRONT][A] = clipMatrix[3] + clipMatrix[2];
        frustum[FRONT][B] = clipMatrix[7] + clipMatrix[6];
        frustum[FRONT][C] = clipMatrix[11] + clipMatrix[10];
        frustum[FRONT][D] = clipMatrix[15] + clipMatrix[14];
        normalizePlane(FRONT);

        frustum[BACK][A] = clipMatrix[3] - clipMatrix[2];
        frustum[BACK][B] = clipMatrix[7] - clipMatrix[6];
        frustum[BACK][C] = clipMatrix[11] - clipMatrix[10];
        frustum[BACK][D] = clipMatrix[15] - clipMatrix[14];
        normalizePlane(BACK);

        Pools.recycleFloat16(clipMatrix);
    }

    private void normalizePlane(int side) {
        float magnitude = (float) Math.sqrt(frustum[side][A] * frustum[side][A] + frustum[side][B] * frustum[side][B] + frustum[side][C] * frustum[side][C]);
        frustum[side][A] /= magnitude;
        frustum[side][B] /= magnitude;
        frustum[side][C] /= magnitude;
        frustum[side][D] /= magnitude;
    }

    public boolean isCubeInFrustum(Vec3d loc, double size) {
        Vec3d l = loc.clone().sub(camera.getViewMatrix().getLocation());
        double x = l.x();
        double y = l.x();
        double z = l.x();
        for(int i = 0; i < 6; i++) {
            if(frustum[i][A] * (x - size) + frustum[i][B] * (y - size) + frustum[i][C] * (z - size) + frustum[i][D] > 0)
                continue;
            if(frustum[i][A] * (x + size) + frustum[i][B] * (y - size) + frustum[i][C] * (z - size) + frustum[i][D] > 0)
                continue;
            if(frustum[i][A] * (x - size) + frustum[i][B] * (y + size) + frustum[i][C] * (z - size) + frustum[i][D] > 0)
                continue;
            if(frustum[i][A] * (x + size) + frustum[i][B] * (y + size) + frustum[i][C] * (z - size) + frustum[i][D] > 0)
                continue;
            if(frustum[i][A] * (x - size) + frustum[i][B] * (y - size) + frustum[i][C] * (z + size) + frustum[i][D] > 0)
                continue;
            if(frustum[i][A] * (x + size) + frustum[i][B] * (y - size) + frustum[i][C] * (z + size) + frustum[i][D] > 0)
                continue;
            if(frustum[i][A] * (x - size) + frustum[i][B] * (y + size) + frustum[i][C] * (z + size) + frustum[i][D] > 0)
                continue;
            if(frustum[i][A] * (x + size) + frustum[i][B] * (y + size) + frustum[i][C] * (z + size) + frustum[i][D] > 0)
                continue;

            Pools.recycle(l);
            return false;
        }
        Pools.recycle(l);
        return true;
    }

    public boolean isSphereInFrustum(Vec3d loc, double radius) {
        Vec3d l = loc.clone().sub(camera.getViewMatrix().getLocation());
        double x = l.x();
        double y = l.y();
        double z = l.z();
        for(int i = 0; i < 6; i++) {
            float[] arr = frustum[i];
            if(arr[A] * x + arr[B] * y + arr[C] * z + arr[D] <= -radius) {
                Pools.recycle(l);
                return false;
            }
        }
        Pools.recycle(l);
        return true;
    }
}