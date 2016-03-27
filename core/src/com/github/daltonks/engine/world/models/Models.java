//Access point to the HashMap that contains models and their names

package com.github.daltonks.engine.world.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Models {
    private static HashMap<String, Model> models = new LinkedHashMap<String, Model>();
    private static ArrayList<Model> modelsList = new ArrayList<Model>();

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
}