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
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.api.registry.metatag.data.MetatagResource;

public abstract class MetatagProvider<R> implements DataProvider {
	protected final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;
	protected final FabricDataOutput dataOutput;
	protected final DataOutput.OutputType outputType;
	protected final RegistryKey<Registry<R>> registryKey;

	protected MetatagProvider(
			FabricDataOutput dataOutput,
			RegistryKey<Registry<R>> registryKey,
			CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture,
			DataOutput.OutputType outputType
	) {
		this.dataOutput = dataOutput;
		this.registriesFuture = registriesFuture;
		this.outputType = outputType;
		this.registryKey = registryKey;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		return this.registriesFuture.thenCompose(lookup -> {
			DataOutput.PathResolver pathResolver = dataOutput.getResolver(outputType, "metatags");
			DynamicOps<JsonElement> ops = RegistryOps.of(JsonOps.INSTANCE, lookup);

			List<MetatagBuilder<?>> metatags = new ArrayList<>();
			configure(metatags::add, lookup);

			return CompletableFuture.allOf(metatags.stream()
					.map(builder -> generate(builder, pathResolver, writer, ops))
					.toArray(CompletableFuture[]::new));
		});
	}

	private <V> CompletableFuture<?> generate(MetatagBuilder<V> builder, DataOutput.PathResolver pathResolver, DataWriter writer, DynamicOps<JsonElement> ops) {
		return CompletableFuture.supplyAsync(() -> {
			Codec<MetatagResource<R, V>> resourceCodec = MetatagResource.resourceCodecOf(builder.metatag);
			return resourceCodec.encodeStart(ops, builder.build())
					.getOrThrow(error -> {
						throw new IllegalStateException("Failed to encode metatag resource: " + error);
					});
		}).thenComposeAsync(encoded -> {
			Identifier registryId = builder.metatag.registryKey().getValue();
			Path metatagPath = pathResolver.resolveJson(builder.metatag.id().withPrefixedPath(registryId.getNamespace() + "/" + registryId.getPath() + "/"));

			return DataProvider.writeToPath(writer, encoded, metatagPath);
		});
	}

	protected abstract void configure(Consumer<MetatagBuilder<?>> provider, RegistryWrapper.WrapperLookup lookup);

	protected final <V> MetatagBuilder<V> create(Metatag<R, V> metatag) {
		return new MetatagBuilder<>(metatag, false);
	}

	protected final <V> MetatagBuilder<V> create(Metatag<R, V> metatag, boolean replace) {
		return new MetatagBuilder<>(metatag, replace);
	}

	@Override
	public String getName() {
		return "Metatags for " + this.registryKey.getValue();
	}

	@SuppressWarnings("unchecked")
	protected RegistryKey<R> reverseLookup(R element) {
		Registry<R> registry = (Registry<R>) Registries.REGISTRIES.get(registryKey.getValue());

		if (registry != null) {
			Optional<RegistryKey<R>> key = registry.getKey(element);
			if (key.isPresent()) return key.get();
		}

		throw new UnsupportedOperationException("Adding objects is not supported by " + getClass());
	}

	protected final class MetatagBuilder<V> {
		private final Metatag<R, V> metatag;
		private final List<Pair<RegistryKey<R>, V>> values = new ArrayList<>();
		private final boolean replace;

		private MetatagBuilder(Metatag<R, V> metatag, boolean replace) {
			this.metatag = metatag;
			this.replace = replace;
		}

		public MetatagBuilder<V> put(R value, V metatagValue) {
			this.values.add(Pair.of(reverseLookup(value), metatagValue));
			return this;
		}

		public MetatagBuilder<V> put(RegistryKey<R> value, V metatagValue) {
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
