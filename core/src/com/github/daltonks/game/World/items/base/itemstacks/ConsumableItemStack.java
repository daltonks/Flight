package com.github.daltonks.game.World.items.base.itemstacks;

import com.github.daltonks.game.World.items.base.Item;
import com.github.daltonks.game.World.entities.livingentities.LivingEntity;

public abstract class ConsumableItemStack extends ItemStack {

    private int amount;

    public ConsumableItemStack(LivingEntity entity, Item item, int amount) {
        super(entity, item);
        this.amount = amount;
    }

    public void onClick() {
        super.onClick();
        if(amount > 0) {
            onConsumed();
            amount--;
        }
    }

    protected abstract void onConsumed();

    public int getAmount() {
        return amount;
    }
}