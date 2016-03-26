// Mainly for my own use
// Takes in .ply models, compresses them, and outputs them as a single file for quick loading into the application

package com.github.daltonks.game.datacompressor;

import com.github.daltonks.engine.datacompressor.Compressor;

import java.io.DataOutputStream;
import java.io.IOException;

public class PaperAirplaneDataCompressor extends Compressor {

    public static void main(String[] args) {
        try {
            PaperAirplaneDataCompressor compressor = new PaperAirplaneDataCompressor();
            compressor.run();
            System.out.println();
            System.out.println("Compressed data!");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public PaperAirplaneDataCompressor() throws IOException {
        super("C:/Users/Dalton/Dropbox/AndroidWorkspace/MyApplication/datapile/",
              "C:/Users/Dalton/Dropbox/AndroidWorkspace/MyApplication/app/src/main/res/raw/data.pile");
    }

    @Override
    public void addConverters(DataOutputStream dataOutputStream, String inputDirectory) throws IOException {
        addConverter(new LivingEntityInfoConverter());
    }
}