package dev.spiritstudios.specter.mixin.registry.reloadable;

import dev.spiritstudios.specter.impl.registry.reloadable.SpecterReloadableRegistriesImpl;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.command.CommandManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DataPackContents.class)
public abstract class DataPackContentsMixin {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries, RegistryWrapper.WrapperLookup registries, FeatureSet enabledFeatures, CommandManager.RegistrationEnvironment environment, List<?> pendingTagLoads, int functionPermissionLevel, CallbackInfo ci) {
		SpecterReloadableRegistriesImpl.setManager(dynamicRegistries.get(ServerDynamicRegistryType.RELOADABLE));
	}
}
