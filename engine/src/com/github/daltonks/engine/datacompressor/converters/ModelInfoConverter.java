package com.github.daltonks.engine.datacompressor.converters;

import com.github.daltonks.engine.datacompressor.models.ModelInfo;
import com.github.daltonks.engine.util.EngineSerializer;

import java.io.*;
import java.util.HashMap;

public class ModelInfoConverter extends Converter {
    private static HashMap<Short, ModelInfo> modelInfos = new HashMap<>();

    public static ModelInfo getModelInfo(short id) {
        if(!modelInfos.containsKey(id)) {
            modelInfos.put(id, new ModelInfo(id));
        }
        return modelInfos.get(id);
    }

    public void read(String inputDirectory) throws IOException {
        File file = new File(inputDirectory + "modelinfo.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        ModelInfo currentModelInfo = null;
        String readLine;
        while((readLine = reader.readLine()) != null) {
            if(!readLine.contains(":")) {
                if(readLine.trim().length() != 0) {
                    String modelName = readLine;
                    byte levelOfDetail = 0;
                    if(modelName.contains("*")) {
                        levelOfDetail = Byte.parseByte(modelName.substring(modelName.indexOf("*") + 1));
                        modelName = modelName.substring(0, modelName.indexOf("*")) + 1;
                    }
                    currentModelInfo = new ModelInfo(ModelConverter.getModelID(modelName));
                    currentModelInfo.levelOfDetail = levelOfDetail;
                    modelInfos.put(currentModelInfo.modelID, currentModelInfo);
                }
            } else {
                String beforeColon = readLine.substring(0, readLine.indexOf(":")).trim();
                String afterColon = readLine.substring(readLine.indexOf(":") + 1).trim();
                switch(beforeColon) {
                    case "collisionModels": {
                        String[] collisionModels = afterColon.split(",");
                        for(int i = 0; i < collisionModels.length; i++) {
                            collisionModels[i] = collisionModels[i].trim();
                        }
                        currentModelInfo.computeCollisionModelShorts(collisionModels);
                        break;
                    }
                    case "mass":
                        currentModelInfo.mass = Float.parseFloat(afterColon);
                        break;
                    case "scale":
                        currentModelInfo.defaultScale = Float.parseFloat(afterColon);
                        break;
                    case "renderBothSides":
                        currentModelInfo.renderBothSides = Boolean.parseBoolean(afterColon);
                        break;
                }
            }
        }
    }

    public void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(modelInfos.size());
        for(ModelInfo info : modelInfos.values()) {
            EngineSerializer.getSerializer().write(dataOutputStream, info);
        }
    }
}