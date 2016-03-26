package com.github.daltonks.game.World.saveandload;

import android.content.Context;
import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.util.EngineSerializer;
import com.github.daltonks.engine.util.Util;
import com.github.daltonks.engine.world.EngineWorld;
import com.github.daltonks.game.World.engineworlds.GameEngineWorld;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class SaveAndLoadHandler {
    public static final int CURRENT_VERSION = 0;

    private static Class<? extends SavePackage> currentSavePackageClass = getSavePackageClass(CURRENT_VERSION);
    private static ArrayList<Field> currentSavePackageClassFields = Util.getAllFields(currentSavePackageClass);

    public static byte saveSlot = 0;

    public static void save() throws IOException {
        Util.startTimer();
        EngineWorld engineWorld = Engine.INSTANCE.getCurrentSubActivity().getEngineWorld();
        if(!(engineWorld instanceof GameEngineWorld)) {
            return;
        }

        String tmpFileName = "tmpsave" + saveSlot;
        String saveFileName = "save" + saveSlot;

        FileOutputStream fos = Engine.INSTANCE.openFileOutput(tmpFileName, Context.MODE_PRIVATE);
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(fos));
        saveData(dos);
        dos.close();

        File tempFile = new File(Engine.INSTANCE.getFilesDir(), tmpFileName);
        File saveFile = new File(Engine.INSTANCE.getFilesDir(), saveFileName);
        if(saveFile.exists()) {
            if(!saveFile.delete()) {
                throw new IOException("Current save file unable to be deleted!");
            }
        }
        if(!tempFile.renameTo(saveFile)) {
            throw new IOException("Temp save file unable to be renamed!");
        }
        System.out.println("Saving: " + Util.endTimer());
    }

    private static void saveData(DataOutputStream dos) throws IOException {
        SavePackage savePackage = null;
        try {
            savePackage = currentSavePackageClass.newInstance();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        dos.writeInt(CURRENT_VERSION);
        savePackage.setVariablesForSaving();
        EngineSerializer.getSerializer().write(dos, savePackage);
    }

    public static void load() throws IOException {
        Util.startTimer();
        FileInputStream fis = Engine.INSTANCE.openFileInput("save" + saveSlot);

        DataInputStream dis = new DataInputStream(new BufferedInputStream(fis));
        int version = dis.readInt();
        SavePackage savePackage = EngineSerializer.getSerializer().read(dis, getSavePackageClass(version));
        while(version != CURRENT_VERSION) {
            version++;
            Class<? extends SavePackage> higherVersionClass = getSavePackageClass(version);
            try {
                SavePackage higherVersionPackage = higherVersionClass.newInstance();
                for(Field lowerField : Util.getAllFields(savePackage.getClass())) {
                    for(Field higherField : currentSavePackageClassFields) {
                        if(lowerField.getName().equals(higherField.getName()) && lowerField.getType() == higherField.getType()) {
                            higherField.set(higherVersionPackage, lowerField.get(savePackage));
                            break;
                        }
                    }
                }
                savePackage.convertToHigherVersion(higherVersionPackage);
                higherVersionPackage.onConvertedFromLowerVersion(savePackage);
                savePackage = higherVersionPackage;
            } catch(Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        savePackage.onLoad();
        dis.close();
        System.out.println("Loading: " + Util.endTimer());
    }

    private static Class<? extends SavePackage> getSavePackageClass(int version) {
        try {
            return (Class<? extends SavePackage>) Class.forName("com.github.daltonks.game.World.saveandload.SavePackage" + version);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}