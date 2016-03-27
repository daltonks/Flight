package com.github.daltonks.game.World.livingentities;

import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.util.EngineSerializer;
import com.github.daltonks.engine.world.models.Model;
import com.github.daltonks.engine.world.models.Models;
import com.github.daltonks.game.datacompressor.LivingEntityInfo;
import com.github.daltonks.game.datacompressor.LivingEntityInfoConverter;

import java.io.DataInput;
import java.util.HashMap;

public class LivingEntityInfos {
    private static HashMap<Model, LivingEntityInfo> livingEntityInfos = new HashMap<Model, LivingEntityInfo>();

    public static void init() {
        DataInput input = Engine.INSTANCE.getDataPileInputStream();
        LivingEntityInfoConverter.ModelIDAndAIInfo[] infos = EngineSerializer.getSerializer().read(input, LivingEntityInfoConverter.ModelIDAndAIInfo[].class);
        for(LivingEntityInfoConverter.ModelIDAndAIInfo info : infos) {
            livingEntityInfos.put(Models.get(info.id), info.info);
        }
    }

    public static LivingEntityInfo getInfo(Model model) {
        return livingEntityInfos.get(model);
    }
}