package com.github.daltonks.engine.datacompressor.converters;

import com.github.daltonks.engine.util.EngineSerializer;

import java.io.*;
import java.util.ArrayList;

public class SubActivityConverter extends Converter {
    private static ArrayList<String> tokens = new ArrayList<>(50);

    @Override
    public void read(String inputDirectory) throws IOException {
        File file = new File(inputDirectory + "subactivities.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while((line = reader.readLine()) != null) {
            line = line.replaceAll(" ", "");
            String[] sections = line.split(",");
            tokens.add(sections[0]);
            tokens.add(sections[1]);
        }
        reader.close();
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        EngineSerializer.getSerializer().write(dataOutputStream, tokens);
    }

    public static short getSubActivityID(String name) {
        for(short i = 0; i < tokens.size(); i+=2) {
            if(tokens.get(i).equals(name)) {
                return (short) (i / 2);
            }
        }
        return -1;
    }
}