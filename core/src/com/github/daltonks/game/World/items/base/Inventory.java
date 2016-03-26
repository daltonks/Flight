package com.github.daltonks.game.World.items.base;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.world.entityComponent.components.Component;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;
import com.github.daltonks.game.World.items.base.itemstacks.ItemStack;

public class Inventory extends Component {
    private int lowestEmptySlot = 0;
    private ItemStack[] items;

    public Inventory(Entity entity, int size) {
        super(entity);
        items = new ItemStack[size];
    }

    @Override
    public void update(EngineState engineState, double delta) {
        for(int i = 0; i < items.length; i++) {
            ItemStack stack = items[i];
            if(stack != null) {
                stack.update(engineState, delta);
            }
        }
    }

    public void addItemStack(ItemStack itemStack) {
        addItemStack(itemStack, lowestEmptySlot);
    }

    public void addItemStack(ItemStack itemStack, int slot) {
        itemStack.setInventorySlot(slot);
        items[slot] = itemStack;
        if(lowestEmptySlot == slot) {
            for(int i = slot + 1; i < items.length; i++) {
                if(items[i] == null) {
                    lowestEmptySlot = i;
                    break;
                }
            }
            //Didn't break, so that means the inventory is full
            lowestEmptySlot = items.length + 1;
        }
    }

    public ItemStack getItemStack(int slot) {
        return items[slot];
    }

    public void removeItemStack(ItemStack itemStack) {
        removeItemStack(itemStack.getInventorySlot());
    }

    public void removeItemStack(int slot) {
        items[slot] = null;
        if(slot < lowestEmptySlot) {
            lowestEmptySlot = slot;
        }
    }

    public boolean isFull() {
        return lowestEmptySlot == items.length + 1;
    }

    public int numOfSlots() {
        return items.length;
    }
}