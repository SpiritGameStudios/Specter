package dev.spiritstudios.specter.impl.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import dev.spiritstudios.specter.api.entity.EntityPart;

public interface EntityPartWorld {
	Int2ObjectMap<EntityPart<?>> specter$parts();
}
