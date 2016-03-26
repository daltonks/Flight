package com.github.daltonks.engine.datacompressor.models;

import com.github.daltonks.engine.datacompressor.converters.ModelConverter;
import serialization.ProguardKeeping;
import serialization.annotations.CanBeNull;

public class ModelInfo implements ProguardKeeping {
    public boolean renderBothSides = false;
    public byte levelOfDetail = 0;
    public short modelID;
    public float mass = 0;
    public float radius;
    public float defaultScale = 1;
    public float minX, minY, minZ;
    public float maxX, maxY, maxZ;
    @CanBeNull
    public short[] collisionModelsShorts;

    public transient float boundingBoxDiagonalLength;

    public ModelInfo(){}

    public ModelInfo(short modelID){
        this.modelID = modelID;
    }

    public void computeCollisionModelShorts(String[] collisionModels) {
        collisionModelsShorts = new short[collisionModels.length];
        for(int i = 0; i < collisionModels.length; i++) {
            String collisionName = collisionModels[i];
            short s;
            if(collisionName.equals("SELF")) {
                s = -1;
            } else if(collisionName.equals("BOX")) {
                s = -2;
            } else if(collisionName.equals("SPHERE")) {
                s = -3;
            } else {
                if(!ModelConverter.isModel(collisionName)) {
                    collisionName = collisionName.substring(0, collisionName.length() - 1);
                }
                s = ModelConverter.getModelID(collisionName);
            }
            collisionModelsShorts[i] = s;
        }
    }

    public boolean hasRigidBodyInfo() {
        return collisionModelsShorts != null;
    }
}