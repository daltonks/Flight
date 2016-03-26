//Imports the data from the static model text files of the levels

package com.github.daltonks.engine.world;

import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.datacompressor.converters.StaticBodyConverter;
import com.github.daltonks.engine.util.EngineSerializer;

import java.io.DataInput;
import java.util.HashMap;

public class WorldStaticEntityInfoImporter {
    private static HashMap<String, StaticBodyConverter.StaticEntityInfo[]> infos = new HashMap<>();

    public static void importInfos() {
        DataInput stream = Engine.INSTANCE.getDataPileInputStream();
        try {
            short num = stream.readShort();
            for(short s = 0; s < num; s++) {
                StaticBodyConverter.SubActivityStaticEntityInfo sasei = EngineSerializer.getSerializer().read(stream, StaticBodyConverter.SubActivityStaticEntityInfo.class);
                infos.put(sasei.subActivityName, sasei.staticEntityInfos);
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static StaticBodyConverter.StaticEntityInfo[] getInfo(String subActivityName) {
        return infos.get(subActivityName);
    }
}