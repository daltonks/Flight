package com.github.daltonks.engine.datacompressor.converters;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Converter {
    public abstract void read(String inputDirectory) throws IOException;
    public abstract void write(DataOutputStream dataOutputStream) throws IOException;
}