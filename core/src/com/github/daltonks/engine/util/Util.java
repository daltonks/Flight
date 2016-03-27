//Mainly contains display and linear algebra math

package com.github.daltonks.engine.util;

import com.badlogic.gdx.Gdx;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Util {

    public static void print(Object... objects) {
        String built = "";
        for(int i = 0; i < objects.length - 1; i++) {
            built += objects[i] + ", ";
        }
        built += objects[objects.length - 1];
        System.out.println(built);
    }

    private static long t;
    public static void startTimer() {
        t = System.currentTimeMillis();
    }

    public static double endTimer() {
        return (System.currentTimeMillis() - t) / 1000.0;
    }

    public static float toOpenGLX(float x) {
        return x * 2.0f / Gdx.graphics.getWidth() - 1;
    }

    public static float toOpenGLY(float y) {
        int displayHeight = Gdx.graphics.getHeight();
        return (displayHeight - y) * 2.0f / displayHeight - 1;
    }

    public static ArrayList<Field> getAllFields(Class clss) {
        ArrayList<Field> fields = new ArrayList<Field>(5);
        getAllFieldsInternal(clss, fields);
        return fields;
    }

    private static void getAllFieldsInternal(Class clss, ArrayList<Field> fieldList) {
        Class superClass = clss.getSuperclass();
        if (superClass != null) {
            getAllFieldsInternal(superClass, fieldList);
        }
        Field[] fields = clss.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAccessible())
                field.setAccessible(true);
            fieldList.add(field);
        }
    }

    public static class ResourceAndShortenedName {
        public String shortenedName;
        public int resourceID;
    }
}