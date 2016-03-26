package com.github.daltonks.engine.datacompressor.converters;

import com.github.daltonks.engine.util.EngineSerializer;
import com.github.daltonks.game.World.physics.CollisionMasks;
import serialization.ProguardKeeping;
import serialization.annotations.CanBeNull;

import java.io.*;
import java.util.ArrayList;

public class StaticBodyConverter extends Converter {

    private static ArrayList<SubActivityStaticEntityInfo> infos = new ArrayList<>();

    private float scaleMultiplier;

    public StaticBodyConverter(float scaleMultiplier) {
        this.scaleMultiplier = scaleMultiplier;
    }

    @Override
    public void read(String inputDirectory) throws IOException {
        String directory = inputDirectory + "worldstaticbodies/";
        File folder = new File(directory);
        for(File file : folder.listFiles()) {
            String subActivityName = file.getName().substring(0, file.getName().indexOf("."));
            BufferedReader reader = new BufferedReader(new FileReader(file));

            SubActivityStaticEntityInfo subActivityStaticEntityInfo = new SubActivityStaticEntityInfo();
            subActivityStaticEntityInfo.subActivityName = subActivityName;
            infos.add(subActivityStaticEntityInfo);

            String readLine;
            //for every line in this reader...
            while((readLine = reader.readLine()) != null) {
                readLine = readLine.trim();
                if(readLine.length() == 0)
                    continue;
                parseLine(readLine, subActivityStaticEntityInfo);
            }
            reader.close();
        }
    }

    private void parseLine(String readLine, SubActivityStaticEntityInfo sasei) {
        StaticEntityInfo entityInfo = new StaticEntityInfo();
        sasei.staticEntityInfosList.add(entityInfo);

        readLine = readLine.replace("'", "");
        String beforeColon = readLine.substring(0, readLine.indexOf(":"));

        //Found special attributes
        if(beforeColon.contains(" ")) {
            String[] attributes = beforeColon.substring(beforeColon.indexOf(" ") + 1).split(" ");
            entityInfo.attributes = new String[attributes.length * 2];
            for(int i = 0; i < attributes.length; i++) {
                String attribute = attributes[i];
                String name = attribute.substring(0, attribute.indexOf("=")).toLowerCase();
                String value = attribute.substring(attribute.indexOf("=") + 1);
                entityInfo.attributes[i * 2] = name;
                entityInfo.attributes[i * 2 + 1] = value;
            }
            beforeColon = beforeColon.substring(0, beforeColon.indexOf(" "));
        }

        if(beforeColon.contains(".")) {
            beforeColon = beforeColon.substring(0, beforeColon.indexOf("."));
        }

        entityInfo.model = ModelConverter.getModelID(beforeColon);

        ModelInfoConverter.getModelInfo(entityInfo.model);
        String afterColon = readLine.substring(readLine.indexOf(":") + 1);
        String[] locs = afterColon.substring(afterColon.indexOf("(") + 1, afterColon.indexOf(")")).split(",");
        entityInfo.x = Float.parseFloat(locs[0].trim()) * scaleMultiplier;
        entityInfo.y = Float.parseFloat(locs[1].trim()) * scaleMultiplier;
        entityInfo.z = Float.parseFloat(locs[2].trim()) * scaleMultiplier;
        String afterLoc = afterColon.substring(afterColon.indexOf(")") + 1).trim();
        if(afterLoc.startsWith("scale")) {
            entityInfo.scale = Float.parseFloat(afterLoc.substring(afterLoc.indexOf("(") + 1, afterLoc.indexOf(")")));
            entityInfo.scale *= scaleMultiplier;
            afterLoc = afterLoc.substring(afterLoc.indexOf(")") + 1).trim();
        }
        String[] rot = afterLoc.substring(afterLoc.indexOf("(") + 1, afterLoc.indexOf(")")).split(",");
        entityInfo.qx = Float.parseFloat(rot[0].trim());
        entityInfo.qy = Float.parseFloat(rot[1].trim());
        entityInfo.qz = Float.parseFloat(rot[2].trim());
        entityInfo.qw = Float.parseFloat(rot[3].trim());
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(infos.size());
        for(SubActivityStaticEntityInfo sasei : infos) {
            sasei.staticEntityInfos = new StaticEntityInfo[sasei.staticEntityInfosList.size()];
            for(int i = 0; i < sasei.staticEntityInfos.length; i++) {
                sasei.staticEntityInfos[i] = sasei.staticEntityInfosList.get(i);
            }
            EngineSerializer.getSerializer().write(dataOutputStream, sasei);
        }
    }

    public static class SubActivityStaticEntityInfo implements ProguardKeeping {
        public String subActivityName;
        public transient ArrayList<StaticEntityInfo> staticEntityInfosList = new ArrayList<>();
        public StaticEntityInfo[] staticEntityInfos;
    }

    public static class StaticEntityInfo implements ProguardKeeping {
        public byte personalCollisionMask = CollisionMasks.WORLD, collidesWithMask = ~CollisionMasks.WORLD;
        public short model;
        public float qx, qy = 1, qz, qw;
        public float scale;
        public double x, y, z;
        @CanBeNull
        public String[] attributes;
    }
}