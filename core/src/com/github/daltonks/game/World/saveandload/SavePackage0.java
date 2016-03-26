package com.github.daltonks.game.World.saveandload;

import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.SortedList;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.entityComponent.components.transformComponents.TransformComponent;
import com.github.daltonks.engine.world.models.Models;
import com.github.daltonks.game.World.engineworlds.GameEngineWorld;
import com.github.daltonks.game.World.items.base.Inventory;
import com.github.daltonks.game.World.items.base.Item;
import com.github.daltonks.game.World.items.base.Items;
import com.github.daltonks.game.World.items.base.itemstacks.ConsumableItemStack;
import com.github.daltonks.game.World.items.base.itemstacks.ItemStack;
import com.github.daltonks.game.World.livingentities.AIEntity;
import com.github.daltonks.game.World.livingentities.LivingEntity;
import com.github.daltonks.game.World.physics.CollisionMasks;
import serialization.ProguardKeeping;

import javax.vecmath.Quat4f;
import java.io.*;
import java.util.ArrayList;

public class SavePackage0 extends SavePackage implements ProguardKeeping {
    public short subActivityID;
    public float gas;
    public EntitySaveData playerData;
    public EntitySaveData[] entitySaveDatas;

    public static class EntitySaveData implements ProguardKeeping {
        public short modelID;
        public double x, y, z;
        public float qx, qy, qz, qw;
        public int health;
        public byte[] inventoryData;
    }

    @Override
    public void setVariablesForSaving() {
        GameEngineWorld gameWorld = GameEngineWorld.INSTANCE;

        subActivityID = Engine.INSTANCE.getCurrentSubActivity().getID();
        //TODO handle gas
        gas = 1;

        //living entities
        ArrayList<LivingEntity> livingEntities = gameWorld.getLivingEntities().getUnderlyingList();
        playerData = getEntityData(gameWorld.getPlayer());
        entitySaveDatas = new EntitySaveData[livingEntities.size() - 1];
        int i = 0;
        for(LivingEntity entity : livingEntities) {
            if(entity != gameWorld.getPlayer()) {
                entitySaveDatas[i] = getEntityData(entity);
                i++;
            }
        }
    }

    private EntitySaveData getEntityData(LivingEntity livingEntity) {
        EntitySaveData data = new EntitySaveData();

        data.modelID = livingEntity.getModelComponent().getModel().getID();
        data.health = livingEntity.getStats().getHealth();

        TransformComponent trans = livingEntity.getTransformComponent();
        Vec3d loc = trans.getLocation();
        data.x = loc.x();
        data.y = loc.y();
        data.z = loc.z();
        Quat4f quat = trans.getRotationQuatNew();
        data.qx = quat.x;
        data.qy = quat.y;
        data.qz = quat.z;
        data.qw = quat.w;
        Pools.recycle(quat);

        /*
            short size of inventory
            for every item slot index:
                boolean empty
                short item
                if consumable:
                    short amount
         */

        int inventoryByteSize = 2;
        Inventory inventory = livingEntity.getInventory();
        for(int i = 0; i < inventory.numOfSlots(); i++) {
            inventoryByteSize++;
            ItemStack itemStack = inventory.getItemStack(i);
            if(itemStack != null) {
                inventoryByteSize += 2;
                if(itemStack instanceof ConsumableItemStack) {
                    inventoryByteSize += 2;
                }
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream(inventoryByteSize);
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeShort(inventory.numOfSlots());
            for(int i = 0; i < inventory.numOfSlots(); i++) {
                ItemStack itemStack = inventory.getItemStack(i);
                dos.writeBoolean(itemStack == null);
                if(itemStack != null) {
                    dos.writeShort(itemStack.getItem().getID());
                    if(itemStack instanceof ConsumableItemStack) {
                        dos.writeShort(((ConsumableItemStack) itemStack).getAmount());
                    }
                }
            }
            data.inventoryData = baos.toByteArray();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    public void onConvertedFromLowerVersion(SavePackage lowerPackage) {

    }

    public void convertToHigherVersion(SavePackage newPackage) {

    }

    @Override
    public void onLoad() {
        EntitySaveData pd = playerData;
        GameEngineWorld.INSTANCE.initPlayer(pd.x, pd.y, pd.z, pd.qx, pd.qy, pd.qz, pd.qw);
        setEntityData(GameEngineWorld.INSTANCE.getPlayer(), pd);

        EngineState engineState = Engine.INSTANCE.getSubActivity("game");
        SortedList<LivingEntity> livingEntities = ((GameEngineWorld) engineState.getEngineWorld()).getLivingEntities();
        for(EntitySaveData data : entitySaveDatas) {
            AIEntity entity = new AIEntity(
                    engineState,
                    data.x, data.y, data.z,
                    data.qx, data.qy, data.qz, data.qw,
                    Models.get(data.modelID),
                    CollisionMasks.ENEMY,
                    null);
            livingEntities.addWithoutSorting(entity);
            setEntityData(entity, data);
        }
        livingEntities.sort();
    }

    private static void setEntityData(LivingEntity entity, EntitySaveData data) {
        entity.getStats().setHealthFromEvent(data.health);

        ByteArrayInputStream baos = new ByteArrayInputStream(data.inventoryData);
        DataInputStream dis = new DataInputStream(baos);
        try {
            short inventorySize = dis.readShort();
            Inventory inventory = new Inventory(entity, inventorySize);
            entity.setInventory(inventory);
            for(short i = 0; i < inventorySize; i++) {
                boolean empty = dis.readBoolean();
                if(!empty) {
                    short itemID = dis.readShort();
                    Item item = Items.getItem(itemID);
                    short amount = 1;
                    if(item.getActivationType() == Item.ActivationType.CONSUMABLE) {
                        amount = dis.readShort();
                    }
                    item.addToInventory(entity, amount);
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}