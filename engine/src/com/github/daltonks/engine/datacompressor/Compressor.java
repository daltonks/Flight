package com.github.daltonks.engine.datacompressor;

import com.github.daltonks.engine.datacompressor.converters.*;

import java.io.*;
import java.util.ArrayList;

public abstract class Compressor {
    private String inputDirectory, outputFilePath;
    private DataOutputStream dataOutputStream;
    private ArrayList<Converter> converters = new ArrayList<>();

    public Compressor(String inputDirectory, String outputFilePath) throws IOException {
        this.inputDirectory = inputDirectory;
        this.outputFilePath = outputFilePath;
    }

    public void run() throws IOException {
        open();
        compress();
        close();
    }

    private void open() throws FileNotFoundException {
        File outputFile = new File(outputFilePath);
        if(outputFile.exists()) outputFile.delete();
        dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
    }

    private void compress() throws IOException {
        addConverter(new SubActivityConverter());
        addConverter(new ModelConverter());
        addConverter(new ModelInfoConverter());
        addConverter(new StaticBodyConverter(50000));
        addConverters(dataOutputStream, inputDirectory);

        for(Converter converter : converters) {
            converter.read(inputDirectory);
        }

        for(Converter converter : converters) {
            converter.write(dataOutputStream);
        }
    }

    public abstract void addConverters(DataOutputStream dataOutputStream, String inputDirectory) throws IOException;

    private void close() throws IOException {
        dataOutputStream.close();
    }

    protected void addConverter(Converter converter) {
        converters.add(converter);
    }
}