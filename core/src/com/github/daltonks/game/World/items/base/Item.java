package com.github.daltonks.game.World.items.base;

import com.github.daltonks.engine.world.models.Model;
import com.github.daltonks.engine.world.models.Models;
import com.github.daltonks.game.World.items.base.itemstacks.ItemStack;
import com.github.daltonks.game.World.livingentities.LivingEntity;

public abstract class Item implements Comparable<Item> {
    private short id;
    private float modelScale;
    private ActivationType activationType;
    private Model model;

    public Item(ActivationType activationType, String modelName, float boundingSphereRadius, int id) {
        this.activationType = activationType;
        model = Models.get(modelName);
        modelScale = boundingSphereRadius / model.getModelInfo().radius;
        this.id = (short) id;
    }

    public void addToInventory(LivingEntity entity, int amount) {
        entity.getInventory().addItemStack(newItemStack(entity, amount));
    }

    protected abstract ItemStack newItemStack(LivingEntity entity, int amount);

    public void setID(short id) {
        this.id = id;
    }

    public short getID() {
        return id;
    }

    public Model getModel() {
        return model;
    }

    public float getModelScale() {
        return modelScale;
    }

    public ActivationType getActivationType() {
        return activationType;
    }

    @Override
    public int compareTo(Item otherItem) {
        if(id < otherItem.id) {
            return -1;
        } else {
            return 1;
        }
    }

    public enum ActivationType {
        STATIC, CONSUMABLE, TOGGLE
    }

}