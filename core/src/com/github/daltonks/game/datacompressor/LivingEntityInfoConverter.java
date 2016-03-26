package com.github.daltonks.game.datacompressor;

import com.github.daltonks.engine.datacompressor.converters.Converter;
import com.github.daltonks.engine.datacompressor.converters.ModelConverter;
import com.github.daltonks.engine.util.EngineSerializer;
import serialization.ProguardKeeping;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class LivingEntityInfoConverter extends Converter {

    private ArrayList<ModelIDAndAIInfo> aiInfos = new ArrayList<>();

    @Override
    public void read(String inputDirectory) throws IOException {
        File file = new File(inputDirectory + "livingentityinfo.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String readLine;
        while((readLine = reader.readLine()) != null) {
            try {
                readLine(readLine);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    ModelIDAndAIInfo currentInfo;
    private void readLine(String line) throws Exception {
        if(line.contains(":")) {
            String fieldName = line.substring(0, line.indexOf(":")).trim();
            String valueString = line.substring(line.indexOf(":") + 1).trim();
            Field field = LivingEntityInfo.class.getField(fieldName);
            if(field.getType() == int.class) {
                field.set(currentInfo.info, Integer.parseInt(valueString));
            } else {
                field.set(currentInfo.info, Float.parseFloat(valueString));
            }
        } else {
            ModelIDAndAIInfo midaaii = new ModelIDAndAIInfo();
            midaaii.id = ModelConverter.getModelID(line);
            midaaii.info = new LivingEntityInfo();
            aiInfos.add(midaaii);
            currentInfo = midaaii;
        }
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        EngineSerializer.getSerializer().write(dataOutputStream, aiInfos);
    }

    public static class ModelIDAndAIInfo implements ProguardKeeping {
        public short id;
        public LivingEntityInfo info;
    }
}