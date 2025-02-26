package dev.spiritstudios.specter.api.registry.metatag.datagen;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.api.registry.metatag.data.MetatagResource;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class MetatagProvider<R> implements DataProvider {
	protected final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;
	protected final FabricDataOutput dataOutput;
	protected final DataOutput.OutputType outputType;
	protected final RegistryKey<Registry<R>> registry;

	protected MetatagProvider(
		FabricDataOutput dataOutput,
		RegistryKey<Registry<R>> registry,
		CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture,
		DataOutput.OutputType outputType
	) {
		this.dataOutput = dataOutput;
		this.registriesFuture = registriesFuture;
		this.outputType = outputType;
		this.registry = registry;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		return this.registriesFuture.thenCompose(lookup -> {
			DataOutput.PathResolver pathResolver = dataOutput.getResolver(outputType, "metatags");
			DynamicOps<JsonElement> ops = RegistryOps.of(JsonOps.INSTANCE, lookup);

			List<MetatagBuilder<R, ?>> metatags = new ArrayList<>();
			configure(metatags::add, lookup);

			return CompletableFuture.allOf(metatags.stream()
				.map(builder -> generate(builder, pathResolver, writer, ops))
				.toArray(CompletableFuture[]::new));
		});
	}

	private <V> CompletableFuture<?> generate(MetatagBuilder<R, V> builder, DataOutput.PathResolver pathResolver, DataWriter writer, DynamicOps<JsonElement> ops) {
		return CompletableFuture.supplyAsync(() -> {
			Codec<MetatagResource<V>> resourceCodec = MetatagResource.resourceCodecOf(builder.metatag);
			return resourceCodec.encodeStart(ops, builder.build())
				.getOrThrow(error -> {
					throw new IllegalStateException("Failed to encode metatag resource: " + error);
				});
		}).thenComposeAsync(encoded -> {
			Identifier registryId = builder.metatag.registry().getKey().getValue();
			Path metatagPath = pathResolver.resolveJson(builder.metatag.id().withPrefixedPath(registryId.getNamespace() + "/" + registryId.getPath() + "/"));

			return DataProvider.writeToPath(writer, encoded, metatagPath);
		});
	}

	protected abstract void configure(Consumer<MetatagBuilder<R, ?>> provider, RegistryWrapper.WrapperLookup lookup);

	protected final <V> MetatagBuilder<R, V> create(Metatag<R, V> metatag) {
		return new MetatagBuilder<>(metatag, false);
	}

	protected final <V> MetatagBuilder<R, V> create(Metatag<R, V> metatag, boolean replace) {
		return new MetatagBuilder<>(metatag, replace);
	}

	@Override
	public String getName() {
		return "Metatags for " + this.registry.getValue();
	}

	protected static final class MetatagBuilder<R, V> {
		private final Metatag<R, V> metatag;
		private final Map<R, V> values = new Object2ObjectOpenHashMap<>();
		private final boolean replace;

		private MetatagBuilder(Metatag<R, V> metatag, boolean replace) {
			this.metatag = metatag;
			this.replace = replace;
		}

		public MetatagBuilder<R, V> put(R value, V metatagValue) {
			this.values.put(value, metatagValue);
			return this;
		}

		private MetatagResource<V> build() {
			return new MetatagResource<>(
				replace,
				values.entrySet().stream()
					.map(entry -> Pair.of(metatag.registry().getId(entry.getKey()), entry.getValue()))
					.filter(pair -> Objects.nonNull(pair.getFirst()))
					.toList()
			);
		}
	}
}
