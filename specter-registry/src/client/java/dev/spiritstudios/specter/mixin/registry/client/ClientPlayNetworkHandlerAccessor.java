package dev.spiritstudios.specter.mixin.registry.client;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayNetworkHandler.class)
public interface ClientPlayNetworkHandlerAccessor {
	@Mutable
	@Accessor
	void setCombinedDynamicRegistries(DynamicRegistryManager.Immutable set);
}
