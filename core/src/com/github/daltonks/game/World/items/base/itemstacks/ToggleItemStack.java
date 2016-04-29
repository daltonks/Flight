package com.github.daltonks.game.World.items.base.itemstacks;

import com.github.daltonks.game.World.items.base.Item;
import com.github.daltonks.game.World.entities.livingentities.LivingEntity;

public abstract class ToggleItemStack extends ItemStack {

    private boolean toggleOn = false;

    public ToggleItemStack(LivingEntity entity, Item item) {
        super(entity, item);
    }

    public void onClick() {
        super.onClick();
        toggleOn = !toggleOn;
        onToggled(toggleOn);
    }

    protected abstract void onToggled(boolean toggleOn);
}