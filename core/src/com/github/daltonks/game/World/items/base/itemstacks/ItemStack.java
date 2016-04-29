package com.github.daltonks.game.World.items.base.itemstacks;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Color;
import com.github.daltonks.engine.util.interfaces.Updatable;
import com.github.daltonks.engine.world.camera.Camera;
import com.github.daltonks.engine.world.entityComponent.components.transformComponents.TransformComponent;
import com.github.daltonks.engine.world.events.EventListener;
import com.github.daltonks.game.World.items.base.Item;
import com.github.daltonks.game.World.entities.livingentities.LivingEntity;

import java.util.ArrayList;

public class ItemStack implements Updatable {
    private int inventorySlot;
    protected Item item;
    private LivingEntity entity;
    private ArrayList<EventListener> eventListeners;

    public ItemStack(LivingEntity entity, Item item) {
        this.item = item;
        this.entity = entity;
    }

    public void onClick() {}

    public void setInventorySlot(int slot) {
        this.inventorySlot = slot;
    }

    public int getInventorySlot() {
        return inventorySlot;
    }

    public Item getItem() {
        return item;
    }

    public void draw(Camera camera, TransformComponent transform) {
        float scale = item.getModelScale();
        item.getModel().draw(
                transform.getLocation(),
                transform.getRotationMatrix(),
                scale, scale, scale,
                Color.WHITE,
                camera
                );
    }

    @Override
    public void update(EngineState engineState, double delta) {

    }

    public LivingEntity getEntity() {
        return entity;
    }
}