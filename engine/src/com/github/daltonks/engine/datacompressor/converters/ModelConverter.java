package com.github.daltonks.engine.datacompressor.converters;

import com.github.daltonks.engine.datacompressor.models.ModelData;
import com.github.daltonks.engine.datacompressor.models.ModelInfo;
import com.github.daltonks.engine.datacompressor.models.Vertex;
import com.github.daltonks.engine.util.EngineSerializer;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.models.Model;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ModelConverter extends Converter {
    private static LinkedHashMap<String, ModelData> models = new LinkedHashMap<>(100);

    @Override
    public void read(String inputDirectory) throws IOException {
        File[] plyFiles = new File(inputDirectory + "plymodels/").listFiles();
        for(File inputFile : plyFiles) {
            String name = inputFile.getName().substring(0, inputFile.getName().lastIndexOf("."));
            ModelData modelData;

            modelData = new ModelData(name);
            modelData.vertices = new ArrayList<>();
            models.put(name, modelData);

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
            boolean headerFinished = false;
            boolean verticesFinished = false;

            int numOfVertices = 0;
            int numOfParsedVertices = 0;
            int index = 0;

            String readLine;
            while ((readLine = br.readLine()) != null) {
                if(!headerFinished) {
                    if(readLine.startsWith("element vertex")) {
                        numOfVertices = Integer.parseInt(readLine.substring(readLine.lastIndexOf(" ") + 1));
                    } else if(readLine.startsWith("element face")) {
                        int numOfFaces = Integer.parseInt(readLine.substring(readLine.lastIndexOf(" ") + 1));
                        modelData.triangleIndices = new int[numOfFaces * 3];
                    } else if(readLine.equals("end_header")) {
                        headerFinished = true;
                    }
                } else if(!verticesFinished) {
                    String[] data = readLine.split(" ");
                    Vertex vertex = new Vertex();
                    vertex.location = new Vec3d();
                    vertex.color = new byte[3];

                    vertex.location.set(Float.parseFloat(data[0]), Float.parseFloat(data[1]), Float.parseFloat(data[2]));

                    if(data.length > 6) {
                        for(byte i = 0; i < 3; i++) {
                            vertex.color[i] = (byte) (Byte.MIN_VALUE + Short.parseShort(data[i + 6]));
                        }
                    } else {
                        for(byte i = 0; i < 3; i++) {
                            vertex.color[i] = Byte.MAX_VALUE;
                        }
                    }

                    modelData.vertices.add(vertex);
                    numOfParsedVertices++;
                    if(numOfParsedVertices == numOfVertices) {
                        verticesFinished = true;
                    }
                } else {
                    if(index > modelData.triangleIndices.length) {
                        break;
                    }
                    String[] data = readLine.split(" ");

                    int[] indexData = new int[data.length];
                    for(int i = 0; i < data.length; i++) {
                        indexData[i] = Integer.parseInt(data[i]);
                    }

                    if(indexData[0] != 3) {
                        System.err.println(name + " isn't in proper triangles!!");
                    }

                    modelData.triangleIndices[index] = indexData[1];
                    modelData.triangleIndices[index + 1] = indexData[2];
                    modelData.triangleIndices[index + 2] = indexData[3];
                    index += 3;
                }
            }
            br.close();
        }
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(models.size());
        for(ModelData modelData : models.values()) {
            String name = modelData.name;
            ModelInfo modelInfo = ModelInfoConverter.getModelInfo(ModelConverter.getModelID(modelData.name));
            try {
                int i = Integer.parseInt(name.charAt(name.length() - 1) + "");
                if(i != 1) {
                    ModelInfo parentInfo = ModelInfoConverter.getModelInfo(ModelConverter.getModelID(name.substring(0, name.length() - 1) + "1"));
                    modelInfo.defaultScale = parentInfo.defaultScale;
                }
            } catch(NumberFormatException e){}

            Vertex firstVertex = modelData.vertices.get(0);
            modelInfo.minX = modelInfo.maxX = firstVertex.location.xf();
            modelInfo.minY = modelInfo.maxY = firstVertex.location.yf();
            modelInfo.minZ = modelInfo.maxZ = firstVertex.location.zf();
            modelInfo.radius = (float) firstVertex.location.length();
            for(int i = 1; i < modelData.vertices.size(); i++) {
                Vertex vertex = modelData.vertices.get(i);
                float x = vertex.location.xf();
                if(x < modelInfo.minX) modelInfo.minX = x;
                if(x > modelInfo.maxX) modelInfo.maxX = x;
                float y = vertex.location.yf();
                if(y < modelInfo.minY) modelInfo.minY = y;
                if(y > modelInfo.maxY) modelInfo.maxY = y;
                float z = vertex.location.zf();
                if(z < modelInfo.minZ) modelInfo.minZ = z;
                if(z > modelInfo.maxZ) modelInfo.maxZ = z;

                float length = (float) vertex.location.length();
                if(length > modelInfo.radius) {
                    modelInfo.radius = length;
                }
            }

            for(int i = 0; i < modelData.triangleIndices.length; i += 3) {
                Vertex first = modelData.vertices.get(modelData.triangleIndices[i]);
                Vertex second = modelData.vertices.get(modelData.triangleIndices[i + 1]);
                Vertex third = modelData.vertices.get(modelData.triangleIndices[i + 2]);

                Vec3d normal = third.location.clone().normal(second.location, first.location);
                first.normal = second.normal = third.normal = normal;
            }

            modelData.vertexData = new float[modelData.vertices.size() * Model.NUM_OF_FLOATS_PER_VERTEX];
            for(int i = 0; i < modelData.vertices.size(); i++) {
                Vertex vertex = modelData.vertices.get(i);
                modelData.vertexData[i * Model.NUM_OF_FLOATS_PER_VERTEX    ] = vertex.location.xf();
                modelData.vertexData[i * Model.NUM_OF_FLOATS_PER_VERTEX + 1] = vertex.location.yf();
                modelData.vertexData[i * Model.NUM_OF_FLOATS_PER_VERTEX + 2] = vertex.location.zf();
                modelData.vertexData[i * Model.NUM_OF_FLOATS_PER_VERTEX + 3] = (((float) vertex.color[0] - Byte.MIN_VALUE) / 255);
                modelData.vertexData[i * Model.NUM_OF_FLOATS_PER_VERTEX + 4] = (((float) vertex.color[1] - Byte.MIN_VALUE) / 255);
                modelData.vertexData[i * Model.NUM_OF_FLOATS_PER_VERTEX + 5] = (((float) vertex.color[2] - Byte.MIN_VALUE) / 255);
                modelData.vertexData[i * Model.NUM_OF_FLOATS_PER_VERTEX + 6] = vertex.normal.xf();
                modelData.vertexData[i * Model.NUM_OF_FLOATS_PER_VERTEX + 7] = vertex.normal.yf();
                modelData.vertexData[i * Model.NUM_OF_FLOATS_PER_VERTEX + 8] = vertex.normal.zf();
            }

            modelData.vertices = null;

            EngineSerializer.getSerializer().write(dataOutputStream, modelData);
        }
    }

    public static short getModelID(String name) {
        short modelIndex = 0;
        for(ModelData modelData : models.values()) {
            if(modelData.name.equals(name)) {
                return modelIndex;
            }
            modelIndex++;
        }
        return -1;
    }

    public static boolean isModel(String name) {
        return models.containsKey(name);
    }
}