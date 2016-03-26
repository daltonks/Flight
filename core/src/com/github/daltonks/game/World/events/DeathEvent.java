package com.github.daltonks.game.World.events;

import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.world.events.Event;
import com.github.daltonks.game.World.livingentities.LivingEntity;
import com.github.daltonks.game.World.livingentities.Player;

public class DeathEvent extends Event {
    public LivingEntity entity;

    public DeathEvent(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public void run() {
        if(entity instanceof Player) {
            Player player = (Player) entity;
            //TODO do something when player dies
        } else {
            Engine.INSTANCE.getCurrentSubActivity().getEngineWorld().removeEntity(entity);
        }
    }
}