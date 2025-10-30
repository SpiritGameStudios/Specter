package dev.spiritstudios.specter.api.registry.metatag.datagen;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.api.registry.metatag.data.MetatagResource;

public abstract class MetatagProvider<R> implements DataProvider {
	protected final CompletableFuture<HolderLookup.Provider> registriesFuture;
	protected final FabricDataOutput dataOutput;
	protected final PackOutput.Target outputType;
	protected final ResourceKey<Registry<R>> registryKey;

	protected MetatagProvider(
			FabricDataOutput dataOutput,
			ResourceKey<Registry<R>> registryKey,
			CompletableFuture<HolderLookup.Provider> registriesFuture,
			PackOutput.Target outputType
	) {
		this.dataOutput = dataOutput;
		this.registriesFuture = registriesFuture;
		this.outputType = outputType;
		this.registryKey = registryKey;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput writer) {
		return this.registriesFuture.thenCompose(lookup -> {
			PackOutput.PathProvider pathResolver = dataOutput.createPathProvider(outputType, "metatags");
			DynamicOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, lookup);

			List<MetatagBuilder<?>> metatags = new ArrayList<>();
			configure(metatags::add, lookup);

			return CompletableFuture.allOf(metatags.stream()
					.map(builder -> generate(builder, pathResolver, writer, ops))
					.toArray(CompletableFuture[]::new));
		});
	}

	private <V> CompletableFuture<?> generate(MetatagBuilder<V> builder, PackOutput.PathProvider pathResolver, CachedOutput writer, DynamicOps<JsonElement> ops) {
		return CompletableFuture.supplyAsync(() -> {
			Codec<MetatagResource<R, V>> resourceCodec = MetatagResource.resourceCodecOf(builder.metatag);
			return resourceCodec.encodeStart(ops, builder.build())
					.getOrThrow(error -> {
						throw new IllegalStateException("Failed to encode metatag resource: " + error);
					});
		}).thenComposeAsync(encoded -> {
			ResourceLocation registryId = builder.metatag.registryKey().location();
			Path metatagPath = pathResolver.json(builder.metatag.id().withPrefix(registryId.getNamespace() + "/" + registryId.getPath() + "/"));

			return DataProvider.saveStable(writer, encoded, metatagPath);
		});
	}

	protected abstract void configure(Consumer<MetatagBuilder<?>> provider, HolderLookup.Provider lookup);

	protected final <V> MetatagBuilder<V> create(Metatag<R, V> metatag) {
		return new MetatagBuilder<>(metatag, false);
	}

	protected final <V> MetatagBuilder<V> create(Metatag<R, V> metatag, boolean replace) {
		return new MetatagBuilder<>(metatag, replace);
	}

	@Override
	public String getName() {
		return "Metatags for " + this.registryKey.location();
	}

	@SuppressWarnings("unchecked")
	protected ResourceKey<R> reverseLookup(R element) {
		Registry<R> registry = (Registry<R>) BuiltInRegistries.REGISTRY.getValue(registryKey.location());

		if (registry != null) {
			Optional<ResourceKey<R>> key = registry.getResourceKey(element);
			if (key.isPresent()) return key.get();
		}

		throw new UnsupportedOperationException("Adding objects is not supported by " + getClass());
	}

	protected final class MetatagBuilder<V> {
		private final Metatag<R, V> metatag;
		private final List<Pair<ResourceKey<R>, V>> values = new ArrayList<>();
		private final boolean replace;

		private MetatagBuilder(Metatag<R, V> metatag, boolean replace) {
			this.metatag = metatag;
			this.replace = replace;
		}

		public MetatagBuilder<V> put(R value, V metatagValue) {
			this.values.add(Pair.of(reverseLookup(value), metatagValue));
			return this;
		}

		public MetatagBuilder<V> put(ResourceKey<R> value, V metatagValue) {
			this.values.add(Pair.of(value, metatagValue));
			return this;
		}

		private MetatagResource<R, V> build() {
			return new MetatagResource<>(
					replace,
					values
			);
		}
	}
}
