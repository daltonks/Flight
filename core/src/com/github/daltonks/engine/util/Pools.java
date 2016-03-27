package com.github.daltonks.engine.util;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.bulletphysics.linearmath.Transform;
import com.github.daltonks.engine.states.inputevents.*;
import com.github.daltonks.engine.util.interfaces.DeepRecycling;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class Pools {
    private static final ArrayList<Vec3d> vec3ds = new ArrayList<Vec3d>();
    public static Vec3d getVec3d() {
        return get(vec3ds, Vec3d.class);
    }

    public static void recycle(Vec3d vec3d) {
        recycle(vec3ds, vec3d);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<Matrix4> matrix4s = new ArrayList<Matrix4>();
    public static Matrix4 getMatrix4() {
        return get(matrix4s, Matrix4.class);
    }

    public static void recycle(Matrix4 mat) {
        recycle(matrix4s, mat);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<Vector3f> vector3fs = new ArrayList<Vector3f>();
    public static Vector3f getVector3f() {
        return get(vector3fs, Vector3f.class);
    }

    public static void recycle(Vector3f vector3f) {
        recycle(vector3fs, vector3f);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<Vector3> vector3s = new ArrayList<Vector3>();
    public static Vector3 getVector3() {
        return get(vector3s, Vector3.class);
    }

    public static void recycle(Vector3 vector3) {
        recycle(vector3s, vector3);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<Color> colors = new ArrayList<Color>();
    public static Color getColor() {
        return get(colors, Color.class);
    }

    public static void recycle(Color color) {
        recycle(colors, color);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<ClickTracker> clickTrackers = new ArrayList<ClickTracker>();
    public static ClickTracker getFingerTracker() {
        return get(clickTrackers, ClickTracker.class);
    }

    public static void recycle(ClickTracker clickTracker) {
        recycle(clickTrackers, clickTracker);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<Transform> transforms = new ArrayList<Transform>();
    public static Transform getTransform() {
        return get(transforms, Transform.class);
    }

    public static void recycle(Transform fingerTracker) {
        recycle(transforms, fingerTracker);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<Quaternion> quaternions = new ArrayList<Quaternion>();
    public static Quaternion getQuaternion() {
        return get(quaternions, Quaternion.class);
    }

    public static void recycle(Quaternion quaternion) {
        recycle(quaternions, quaternion);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<Quat4f> quats = new ArrayList<Quat4f>();
    public static Quat4f getQuat4f() {
        return get(quats, Quat4f.class);
    }

    public static void recycle(Quat4f quat) {
        recycle(quats, quat);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<float[]> float4s = new ArrayList<float[]>();
    public static float[] getFloat4() {
        return getArray(float4s, float[].class, 4);
    }

    public static void recycleFloat4(float[] arr) {
        recycle(float4s, arr);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<float[]> float16s = new ArrayList<float[]>();
    public static float[] getFloat16() {
        return getArray(float16s, float[].class, 16);
    }

    public static void recycleFloat16(float[] arr) {
        recycle(float16s, arr);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<ClickDownEvent> clickDownEvents = new ArrayList<ClickDownEvent>();
    public static ClickDownEvent getDownTouchEvent() {
        return get(clickDownEvents, ClickDownEvent.class);
    }

    public static void recycleDownTouchEvent(ClickDownEvent touchEvent) {
        recycle(clickDownEvents, touchEvent);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<DragEvent> dragEvents = new ArrayList<DragEvent>();
    public static DragEvent getMoveTouchEvent() {
        return get(dragEvents, DragEvent.class);
    }

    public static void recycleMoveTouchEvent(DragEvent touchEvent) {
        recycle(dragEvents, touchEvent);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<ClickUpEvent> clickUpEvents = new ArrayList<ClickUpEvent>();
    public static ClickUpEvent getUpTouchEvent() {
        return get(clickUpEvents, ClickUpEvent.class);
    }

    public static void recycleUpTouchEvent(ClickUpEvent touchEvent) {
        recycle(clickUpEvents, touchEvent);
    }

    //----------------------------------------------------------------------------------------------

    public static void recycle(InputRunnable touchEvent) {
        if(touchEvent instanceof DragEvent) {
            recycleMoveTouchEvent((DragEvent) touchEvent);
        } else if(touchEvent instanceof ClickDownEvent) {
            recycleDownTouchEvent((ClickDownEvent) touchEvent);
        } else if(touchEvent instanceof ClickUpEvent){
            recycleUpTouchEvent((ClickUpEvent) touchEvent);
        }
    }

    //----------------------------------------------------------------------------------------------

    public static <T> T get(ArrayList<T> list, Class<T> clss) {
        synchronized(list) {
            if(!list.isEmpty()) {
                return list.remove(list.size() - 1);
            }
        }

        try {
            Constructor<T> constructor = clss.getDeclaredConstructor();
            if(!constructor.isAccessible())
                constructor.setAccessible(true);
            return constructor.newInstance();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(0);
            return null;
        }
    }

    public static <T> T getArray(ArrayList<T> list, Class<T> arrayClass, int size) {
        Class componentClass = arrayClass.getComponentType();
        synchronized(list) {
            if(!list.isEmpty()) {
                return list.remove(list.size() - 1);
            }
        }
        return (T) Array.newInstance(componentClass, size);
    }

    public static void recycle(ArrayList list, Object object) {
        if(object instanceof DeepRecycling)
            ((DeepRecycling) object).beforeRecycle();

        synchronized(list) {
            list.add(object);
        }
    }
}