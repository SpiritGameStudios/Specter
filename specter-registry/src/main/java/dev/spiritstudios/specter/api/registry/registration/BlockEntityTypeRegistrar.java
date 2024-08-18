package dev.spiritstudios.specter.api.registry.registration;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface BlockEntityTypeRegistrar extends MinecraftRegistrar<BlockEntityType<?>> {
	@Override
	default Registry<BlockEntityType<?>> getRegistry() {
		return Registries.BLOCK_ENTITY_TYPE;
	}
}
