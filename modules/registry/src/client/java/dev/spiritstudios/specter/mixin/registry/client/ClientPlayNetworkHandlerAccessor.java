package dev.spiritstudios.specter.mixin.registry.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;

@Mixin(ClientPacketListener.class)
public interface ClientPlayNetworkHandlerAccessor {
	@Mutable
	@Accessor
	void setRegistryAccess(RegistryAccess.Frozen set);
}
