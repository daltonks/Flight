package com.github.daltonks.engine.util;

import com.github.daltonks.engine.datacompressor.models.ModelData;
import serialization.DataSerialization;
import serialization.writerreaders.WriterReader;

import java.io.DataInput;
import java.io.DataOutput;

public class EngineSerializer {
    private static DataSerialization serializer = new DataSerialization(true);

    static {
        WriterReader modelDataWriterReader = new WriterReader<ModelData>() {
            @Override
            public void write(DataSerialization dataSerialization, DataOutput dataOutput, ModelData modelData) throws Exception {
                dataOutput.writeUTF(modelData.name);
                dataOutput.writeInt(modelData.vertexData.length);
                for(int i = 0; i < modelData.vertexData.length; i++) {
                    dataOutput.writeFloat(modelData.vertexData[i]);
                }
                dataOutput.writeInt(modelData.triangleIndices.length);
                for(int i = 0; i < modelData.triangleIndices.length; i++) {
                    dataOutput.writeShort(modelData.triangleIndices[i]);
                }
            }

            @Override
            public ModelData readInternal(DataSerialization dataSerialization, DataInput dataInput, Class[] classes, int typeOffset) throws Exception {
                ModelData modelData = new ModelData(dataInput.readUTF());
                modelData.vertexData = new float[dataInput.readInt()];
                for(int i = 0; i < modelData.vertexData.length; i++) {
                    modelData.vertexData[i] = dataInput.readFloat();
                }
                modelData.triangleIndices = new short[dataInput.readInt()];
                for(int i = 0; i < modelData.triangleIndices.length; i++) {
                    modelData.triangleIndices[i] = dataInput.readShort();
                }
                return modelData;
            }
        };
        serializer.setWriterReader(ModelData.class, modelDataWriterReader);
    }

    public static DataSerialization getSerializer() {
        return serializer;
    }
}