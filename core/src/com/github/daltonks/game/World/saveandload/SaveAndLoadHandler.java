package com.github.daltonks.game.World.saveandload;

import com.badlogic.gdx.Gdx;
import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.util.EngineSerializer;
import com.github.daltonks.engine.util.Util;
import com.github.daltonks.engine.world.EngineWorld;
import com.github.daltonks.game.World.engineworlds.GameEngineWorld;
import com.badlogic.gdx.files.FileHandle;

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

        String tmpFileName = "saves/tmpsave" + saveSlot + ".sv";
        String saveFileName = "saves/save" + saveSlot + ".sv";

        FileHandle tempHandler = Gdx.files.local(tmpFileName);
        DataOutputStream dos = new DataOutputStream(tempHandler.write(false, 8192));
        saveData(dos);
        dos.close();

        FileHandle saveHandler = Gdx.files.local(saveFileName);
        if(saveHandler.exists()) {
            if(!saveHandler.delete()) {
                throw new IOException("Current save file unable to be deleted!");
            }
        }
        tempHandler.copyTo(saveHandler);
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

    public static boolean load() throws IOException {
        Util.startTimer();
        FileHandle saveHandler = Gdx.files.local("saves/save" + saveSlot + ".sv");
        if(!saveHandler.exists()) {
            return false;
        }
        DataInputStream dis = new DataInputStream(saveHandler.read(8192));
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
        return true;
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