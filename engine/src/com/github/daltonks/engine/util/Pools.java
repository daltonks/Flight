package com.github.daltonks.engine.util;

import com.bulletphysics.linearmath.Transform;
import com.github.daltonks.engine.states.touchevents.*;
import com.github.daltonks.engine.util.interfaces.DeepRecycling;
import com.github.daltonks.engine.util.interfaces.EngineRunnable;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class Pools {
    private static final ArrayList<Vec3d> vec3ds = new ArrayList<>();
    public static Vec3d getVec3d() {
        return get(vec3ds, Vec3d.class);
    }

    public static void recycle(Vec3d vec3d) {
        recycle(vec3ds, vec3d);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<Vector3f> vector3fs = new ArrayList<>();
    public static Vector3f getVector3f() {
        return get(vector3fs, Vector3f.class);
    }

    public static void recycle(Vector3f vector3f) {
        recycle(vector3fs, vector3f);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<Matrix3f> matrix3fs = new ArrayList<>();
    public static Matrix3f getMatrix3f() {
        return get(matrix3fs, Matrix3f.class);
    }

    public static void recycle(Matrix3f matrix3f) {
        recycle(matrix3fs, matrix3f);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<Color> colors = new ArrayList<>();
    public static Color getColor() {
        return get(colors, Color.class);
    }

    public static void recycle(Color color) {
        recycle(colors, color);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<FingerTracker> fingerTrackers = new ArrayList<>();
    public static FingerTracker getFingerTracker() {
        return get(fingerTrackers, FingerTracker.class);
    }

    public static void recycle(FingerTracker fingerTracker) {
        recycle(fingerTrackers, fingerTracker);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<Transform> transforms = new ArrayList<>();
    public static Transform getTransform() {
        return get(transforms, Transform.class);
    }

    public static void recycle(Transform fingerTracker) {
        recycle(transforms, fingerTracker);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<AxisAngle4d> axisAngles = new ArrayList<>();
    public static AxisAngle4d getAxisAngle() {
        return get(axisAngles, AxisAngle4d.class);
    }

    public static void recycle(AxisAngle4d axisAngle) {
        recycle(axisAngles, axisAngle);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<Quat4f> quats = new ArrayList<>();
    public static Quat4f getQuat4f() {
        return get(quats, Quat4f.class);
    }

    public static void recycle(Quat4f quat) {
        recycle(quats, quat);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<float[]> float4s = new ArrayList<>();
    public static float[] getFloat4() {
        return getArray(float4s, float[].class, 4);
    }

    public static void recycleFloat4(float[] arr) {
        recycle(float4s, arr);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<float[]> float16s = new ArrayList<>();
    public static float[] getFloat16() {
        return getArray(float16s, float[].class, 16);
    }

    public static void recycleFloat16(float[] arr) {
        recycle(float16s, arr);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<DownTouchEvent> downTouchEvents = new ArrayList<>();
    public static DownTouchEvent getDownTouchEvent() {
        return get(downTouchEvents, DownTouchEvent.class);
    }

    public static void recycleDownTouchEvent(DownTouchEvent touchEvent) {
        recycle(downTouchEvents, touchEvent);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<MoveTouchEvent> moveTouchEvents = new ArrayList<>();
    public static MoveTouchEvent getMoveTouchEvent() {
        return get(moveTouchEvents, MoveTouchEvent.class);
    }

    public static void recycleMoveTouchEvent(MoveTouchEvent touchEvent) {
        recycle(moveTouchEvents, touchEvent);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<UpTouchEvent> upTouchEvents = new ArrayList<>();
    public static UpTouchEvent getUpTouchEvent() {
        return get(upTouchEvents, UpTouchEvent.class);
    }

    public static void recycleUpTouchEvent(UpTouchEvent touchEvent) {
        recycle(upTouchEvents, touchEvent);
    }

    //----------------------------------------------------------------------------------------------

    private static final ArrayList<BackPressedEvent> backPressedEvents = new ArrayList<>();
    public static BackPressedEvent getBackPressedEvent() {
        return get(backPressedEvents, BackPressedEvent.class);
    }

    public static void recycleBackPressedEvent(BackPressedEvent event) {
        recycle(backPressedEvents, event);
    }

    //----------------------------------------------------------------------------------------------

    public static void recycle(EngineRunnable touchEvent) {
        if(touchEvent instanceof MoveTouchEvent) {
            recycleMoveTouchEvent((MoveTouchEvent) touchEvent);
        } else if(touchEvent instanceof DownTouchEvent) {
            recycleDownTouchEvent((DownTouchEvent) touchEvent);
        } else if(touchEvent instanceof  UpTouchEvent){
            recycleUpTouchEvent((UpTouchEvent) touchEvent);
        } else {
            recycleBackPressedEvent((BackPressedEvent) touchEvent);
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