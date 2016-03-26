package com.github.daltonks.game.World.items.base;

import com.github.daltonks.engine.datacompressor.models.ModelInfo;
import com.github.daltonks.engine.world.models.Model;
import com.github.daltonks.engine.world.models.Models;
import com.github.daltonks.game.World.items.BasicBulletShooter;

import java.util.HashMap;

public class Items {
    public static Item BASIC_BULLET_SHOOTER;

    private static HashMap<Short, Item> idsAndItems = new HashMap<>();

    public static void init() {
        Model itemSlotModel = Models.get("itemslot");
        ModelInfo info = itemSlotModel.getModelInfo();
        float itemSlotWidth = (info.maxX - info.minX) * itemSlotModel.getModelInfo().defaultScale / 2 * .8f;

        BASIC_BULLET_SHOOTER = add(new BasicBulletShooter(itemSlotWidth, 0));
    }

    private static Item add(Item item) {
        idsAndItems.put(item.getID(), item);
        return item;
    }

    public static Item getItem(short id) {
        return idsAndItems.get(id);
    }
}