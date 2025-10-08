package dev.spiritstudios.specter.impl.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import dev.spiritstudios.specter.api.entity.EntityPart;

public interface EntityPartWorld {
	default Int2ObjectMap<EntityPart<?>> specter$getParts() {
		throw new UnsupportedOperationException("Injected interface should be implemented by mixin!");
	}
}
