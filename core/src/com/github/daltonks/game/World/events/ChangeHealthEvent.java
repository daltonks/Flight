package com.github.daltonks.game.World.events;

import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;
import com.github.daltonks.engine.world.events.Event;
import com.github.daltonks.game.World.entities.livingentities.LivingEntity;
import com.github.daltonks.game.World.entities.livingentities.LivingEntityStats;

public class ChangeHealthEvent extends Event {
    public int change;
    public Entity source;
    public LivingEntity target;

    public ChangeHealthEvent(Entity source, LivingEntity target, int change) {
        this.source = source;
        this.target = target;
        this.change = change;
    }

    @Override
    public void run() {
        LivingEntityStats livingEntityStats = target.getStats();
        livingEntityStats.setHealthFromEvent(livingEntityStats.getHealth() + change);
        if(livingEntityStats.getHealth() > livingEntityStats.getMaxHealth()) {
            livingEntityStats.setHealthFromEvent(livingEntityStats.getMaxHealth());
        } else if(livingEntityStats.getHealth() <= 0) {
            livingEntityStats.setHealthFromEvent(0);
            Engine.INSTANCE.getCurrentSubActivity().getEventSystemContainer().performEvent(new DeathEvent(target));
        }
    }
}