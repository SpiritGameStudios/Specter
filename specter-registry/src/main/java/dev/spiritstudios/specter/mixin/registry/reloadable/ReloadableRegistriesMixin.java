package dev.spiritstudios.specter.mixin.registry.reloadable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import dev.spiritstudios.specter.impl.registry.reloadable.SpecterReloadableRegistriesImpl;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableRegistries.class)
public abstract class ReloadableRegistriesMixin {
	@Shadow
	@Final
	private static Gson GSON;

	@Shadow
	@Final
	private static Logger LOGGER;

	@Shadow
	@Final
	private static RegistryEntryInfo DEFAULT_REGISTRY_ENTRY_INFO;

	@WrapOperation(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;combineSafe(Ljava/util/List;)Ljava/util/concurrent/CompletableFuture;"))
	private static <V> CompletableFuture<List<V>> addModdedReloadableRegistries(
		List<CompletableFuture<? extends MutableRegistry<?>>> futures,
		Operation<CompletableFuture<List<V>>> original,
		@Local DynamicRegistryManager.Immutable dynamicRegistryManager,
		@Local RegistryOps<JsonElement> ops,
		@Local(argsOnly = true) ResourceManager resourceManager,
		@Local(argsOnly = true) Executor prepareExecutor
	) {
		futures = new ArrayList<>(futures);
		for (SpecterReloadableRegistriesImpl.ReloadableRegistryInfo<?> registryInfo : SpecterReloadableRegistriesImpl.reloadableRegistries()) {
			futures.add(CompletableFuture.supplyAsync(
				() -> addElement(registryInfo, ops, resourceManager),
				prepareExecutor
			));
		}

		return original.call(futures);
	}

	@SuppressWarnings("unchecked")
	@Unique
	private static <T> MutableRegistry<T> addElement(SpecterReloadableRegistriesImpl.ReloadableRegistryInfo<T> registryInfo, RegistryOps<JsonElement> ops, ResourceManager resourceManager) {
		MutableRegistry<T> registry = new SimpleRegistry<>(registryInfo.key(), Lifecycle.experimental());
		Map<Identifier, JsonElement> elements = new Object2ObjectOpenHashMap<>();
		JsonDataLoader.load(resourceManager, RegistryKeys.getPath(registryInfo.key()), GSON, elements);

		elements.forEach((id, element) -> {
			LOGGER.info(element.toString());

			DataResult<?> dataResult = registryInfo.codec().parse(ops, element);
			dataResult.error().ifPresent(error ->
				LOGGER.error("Couldn't parse element {}/{} - {}", registryInfo.key().getValue(), id, error.message()));

			dataResult.result()
				.ifPresent(value ->
					registry.add(
						RegistryKey.of(registryInfo.key(), id),
						(T) value,
						DEFAULT_REGISTRY_ENTRY_INFO
					));
		});

		return registry;
	}
}
