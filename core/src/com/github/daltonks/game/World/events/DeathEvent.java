package com.github.daltonks.game.World.events;

import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.world.events.Event;
import com.github.daltonks.game.World.engineworlds.GameEngineWorld;
import com.github.daltonks.game.World.entities.ExplodeEntity;
import com.github.daltonks.game.World.entities.livingentities.LivingEntity;
import com.github.daltonks.game.World.entities.livingentities.Player;

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
            GameEngineWorld.INSTANCE.removeEntity(entity);
            GameEngineWorld.INSTANCE.addEntity(new ExplodeEntity(Engine.INSTANCE.getCurrentSubActivity(), entity));
        }
    }
}