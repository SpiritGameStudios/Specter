package dev.spiritstudios.specter.mixin.registry.reloadable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.ReloadableRegistries;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.impl.registry.reloadable.SpecterReloadableRegistriesImpl;

@Mixin(ReloadableRegistries.class)
public abstract class ReloadableRegistriesMixin {
	@Shadow
	@Final
	private static RegistryEntryInfo DEFAULT_REGISTRY_ENTRY_INFO;

	@WrapOperation(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;combineSafe(Ljava/util/List;)Ljava/util/concurrent/CompletableFuture;"))
	private static <V> CompletableFuture<List<V>> addModdedReloadableRegistries(
		List<CompletableFuture<? extends MutableRegistry<?>>> futures,
		Operation<CompletableFuture<List<V>>> original,
		@Local(argsOnly = true) ResourceManager resourceManager,
		@Local RegistryOps<JsonElement> ops,
		@Local(argsOnly = true) Executor prepareExecutor
	) {
		futures = new ArrayList<>(futures);
		for (SpecterReloadableRegistriesImpl.ReloadableRegistryInfo<?> info : SpecterReloadableRegistriesImpl.reloadableRegistries().values()) {
			futures.add(prepareSpecter(info, ops, resourceManager, prepareExecutor));
		}

		return original.call(futures);
	}

	@Unique
	private static <T> CompletableFuture<MutableRegistry<?>> prepareSpecter(SpecterReloadableRegistriesImpl.ReloadableRegistryInfo<T> registryInfo, RegistryOps<JsonElement> ops, ResourceManager resourceManager, Executor prepareExecutor) {
		return CompletableFuture.supplyAsync(() -> {
			MutableRegistry<T> registry = new SimpleRegistry<>(registryInfo.key(), Lifecycle.experimental());
			Map<Identifier, T> elements = new Object2ObjectOpenHashMap<>();

			JsonDataLoader.load(resourceManager, registryInfo.key(), ops, registryInfo.codec(), elements);

			elements.forEach((id, value) -> registry.add(
				RegistryKey.of(registryInfo.key(), id),
				value,
				DEFAULT_REGISTRY_ENTRY_INFO
			));

			TagGroupLoader.loadInitial(resourceManager, registry);

			return registry;
		}, prepareExecutor);
	}
}
