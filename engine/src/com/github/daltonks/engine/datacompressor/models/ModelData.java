package com.github.daltonks.engine.datacompressor.models;

import serialization.ProguardKeeping;

import java.util.ArrayList;

public class ModelData implements ProguardKeeping {
    public String name;
    public float[] vertexData;
    public int[] triangleIndices;

    public transient ArrayList<Vertex> vertices;

    public ModelData() {}

    public ModelData(String name) {
        this.name = name;
    }
}