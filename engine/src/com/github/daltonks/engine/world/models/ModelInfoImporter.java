package com.github.daltonks.engine.world.models;

import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.datacompressor.models.ModelInfo;
import com.github.daltonks.engine.util.EngineSerializer;

import java.io.DataInputStream;

public class ModelInfoImporter {
    public static void importModelInfo() {
        DataInputStream inputStream = Engine.INSTANCE.getDataPileInputStream();
        try {
            short numOfInfos = inputStream.readShort();
            for(short s = 0; s < numOfInfos; s++) {
                ModelInfo info = EngineSerializer.getSerializer().read(inputStream, ModelInfo.class);
                if(info.modelID == -1) {
                    System.out.println("There's model info you have to remove!");
                    continue;
                }
                if(info.levelOfDetail == 0) {
                    Models.get(info.modelID).setModelInfo(info);
                } else {
                    String firstLODModelName = Models.get(info.modelID).getName();
                    for(int i = 1; i <= info.levelOfDetail; i++) {
                        Models.get(firstLODModelName.substring(0, firstLODModelName.length() - 1) + i).setModelInfo(info);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}