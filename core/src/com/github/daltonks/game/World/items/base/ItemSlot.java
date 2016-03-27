package com.github.daltonks.game.World.items.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.world.camera.Camera;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;
import com.github.daltonks.engine.world.models.Models;
import com.github.daltonks.engine.world.ui.UIBoundsComponent;
import com.github.daltonks.engine.world.ui.UIEntity;
import com.github.daltonks.game.World.items.base.itemstacks.ItemStack;

public class ItemSlot extends UIEntity {
    private int slot;
    private Inventory inventory;

    public ItemSlot(EngineState engineState, Inventory inventory, int slot) {
        super(engineState, Models.get("itemslot"));
        this.inventory = inventory;
        this.slot = slot;
        this.setUIBoundsComponent(new ItemSlotBoundsComponent(this));
    }

    public void draw(Camera camera) {
        super.draw(camera);
        ItemStack itemStack = getItemStack();
        if(itemStack != null) {
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
            itemStack.draw(camera, getTransformComponent());
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        }
    }

    public ItemStack getItemStack() {
        return inventory.getItemStack(slot);
    }

    public static class ItemSlotBoundsComponent extends UIBoundsComponent {
        public ItemSlotBoundsComponent(Entity entity) {
            super(entity);
        }

        public boolean onCollidedUp(EngineState engineState, float openGLX, float openGLY) {
            ItemStack itemStack = ((ItemSlot) getEntity()).getItemStack();
            if(itemStack != null)
                itemStack.onClick();
            return true;
        }
    }
}