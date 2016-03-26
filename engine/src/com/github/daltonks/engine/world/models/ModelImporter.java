package com.github.daltonks.engine.world.models;

import android.opengl.GLES20;
import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.datacompressor.models.ModelData;
import com.github.daltonks.engine.util.EngineSerializer;
import serialization.writerreaders.WriterReader;

import java.io.DataInputStream;
import java.io.IOException;

public class ModelImporter {

    public static void importAllModels() {
        DataInputStream inputStream = Engine.INSTANCE.getDataPileInputStream();
        try {
            WriterReader modelDataWriterReader = EngineSerializer.getSerializer().getWriterReader(ModelData.class);
            short numOfModels = inputStream.readShort();
            for(short i = 0; i < numOfModels; i++) {
                Model model = loadNextModelFromInputStream(inputStream, i, modelDataWriterReader);
                Models.addModel(model);
            }
            currentData = null;
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static ModelData currentData;
    private static Model loadNextModelFromInputStream(DataInputStream inputStream, short id, WriterReader modelDataWriterReader) throws IOException {
        currentData = EngineSerializer.getSerializer().read(inputStream, ModelData.class, modelDataWriterReader);

        Model model = new Model(currentData.name);
        model.setID(id);

        model.updateIndicesAndDrawingMode(currentData.triangleIndices, Gdx.gl.GL_TRIANGLES);

        int numOfVertices = currentData.vertexData.length;
        model.updateNumOfVertices(numOfVertices);
        model.addVertices(currentData.vertexData);

        return model;
    }
}