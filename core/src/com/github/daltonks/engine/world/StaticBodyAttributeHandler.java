package com.github.daltonks.engine.world;

import com.github.daltonks.engine.datacompressor.converters.StaticBodyConverter;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;

public interface StaticBodyAttributeHandler {
    void handle(Entity entity, StaticBodyConverter.StaticEntityInfo entityInfo, String lowerCaseKey, String value);
}