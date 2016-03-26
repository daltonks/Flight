//Access point to the HashMap that contains models and their names

package com.github.daltonks.engine.world.models;

import android.opengl.GLES20;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Models {
    private static HashMap<String, Model> models = new LinkedHashMap<>();
    private static ArrayList<Model> modelsList = new ArrayList<>();

    public static Model get(String name) {
        return models.get(name);
    }

    public static Model get(short id) {
        return modelsList.get(id);
    }

    public static void addModel(Model model) {
        models.put(model.getName(), model);
        modelsList.add(model);
    }

    public static void bufferModelsToGL() {
        int[] buffers = new int[models.size() * 2];
        Gdx.gl.glGenBuffers(buffers.length, buffers, 0);

        int i = 0;
        for(Model model : models.values()) {
            model.setBufferReferences(buffers[i], buffers[i + 1]);
            i += 2;
        }
    }
}