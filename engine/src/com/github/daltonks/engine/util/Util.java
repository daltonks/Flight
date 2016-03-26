//Mainly contains display and linear algebra math

package com.github.daltonks.engine.util;

import com.badlogic.gdx.Gdx;
import wcdtq7.gl.goo.game.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;

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
        ArrayList<Field> fields = new ArrayList<>(5);
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

    public static LinkedList<ResourceAndShortenedName> getAllResourcesThatStartWith(String sequence) {
        Field[] fields = R.raw.class.getFields();
        LinkedList<ResourceAndShortenedName> list = new LinkedList<>();
        // loop for every file in raw folder
        for(Field field : fields) {
            int resourceID = 0;
            try {
                resourceID = field.getInt(field);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            String resourceName = field.getName();
            if(resourceName.startsWith(sequence)) {
                ResourceAndShortenedName rasn = new ResourceAndShortenedName();
                rasn.shortenedName = field.getName().substring(sequence.length());
                rasn.resourceID = resourceID;
                list.add(rasn);
            }
        }
        return list;
    }

    public static class ResourceAndShortenedName {
        public String shortenedName;
        public int resourceID;
    }
}