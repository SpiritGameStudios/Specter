package dev.spiritstudios.specter.mixin.registry.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.registry.DynamicRegistryManager;

@Mixin(ClientPlayNetworkHandler.class)
public interface ClientPlayNetworkHandlerAccessor {
	@Mutable
	@Accessor
	void setCombinedDynamicRegistries(DynamicRegistryManager.Immutable set);
}
