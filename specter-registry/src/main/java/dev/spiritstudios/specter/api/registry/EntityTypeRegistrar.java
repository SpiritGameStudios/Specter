package dev.spiritstudios.specter.api.registry;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface EntityTypeRegistrar extends MinecraftRegistrar<EntityType<?>> {
	@Override
	default Registry<EntityType<?>> getRegistry() {
		return Registries.ENTITY_TYPE;
	}
}
