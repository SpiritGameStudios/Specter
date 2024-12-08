package dev.spiritstudios.specter.impl.registry.metatag.data;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.registry.SpecterRegistry;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagHolder;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagValueHolder;
import dev.spiritstudios.specter.impl.registry.metatag.network.MetatagSyncS2CPayload;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MetatagReloader implements SimpleResourceReloadListener<Map<Metatag<?, ?>, MetatagMap<?, ?>>> {
	private final ResourceType side;

	public MetatagReloader(ResourceType side) {
		this.side = side;
	}

	private <R, V> MetatagMap<R, V> createMap(Metatag<R, V> metatag) {
		return new MetatagMap<>(metatag.registry(), metatag);
	}

	@Override
	public CompletableFuture<Map<Metatag<?, ?>, MetatagMap<?, ?>>> load(ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<Metatag<?, ?>, MetatagMap<?, ?>> metatagMaps = new Object2ObjectOpenHashMap<>();

			for (RegistryEntry<MutableRegistry<?>> entry : Registries.ROOT.getIndexedEntries()) { // For each registry
				if (entry.getKey().isEmpty()) continue;
				Identifier id = entry.getKey().get().getValue();
				String metatagPath = id.getNamespace() + "/" + id.getPath();

				Map<Identifier, List<Resource>> metatagResources = manager.findAllResources(
					"metatags/" + metatagPath,
					string -> string.getPath().endsWith(".json")
				);

				if (metatagResources.isEmpty()) continue;

				parseMetatagResources(metatagResources, entry.value(), metatagMaps);
			}

			return metatagMaps;
		}, executor);
	}

	private void parseMetatagResources(Map<Identifier, List<Resource>> resources, Registry<?> registry, Map<Metatag<?, ?>, MetatagMap<?, ?>> metatagMaps) {
		for (Map.Entry<Identifier, List<Resource>> resource : resources.entrySet()) {
			Identifier metatagResourceId = resource.getKey();

			// Transform the path into the Metatag ID (e.g. specter:metatags/minecraft/block/strippable.json -> specter:strippable)
			String path = metatagResourceId.getPath();
			path = path.substring(path.lastIndexOf('/') + 1);
			path = path.substring(0, path.lastIndexOf('.'));
			Identifier metatagId = Identifier.of(metatagResourceId.getNamespace(), path);

			List<Resource> metatagResources = resource.getValue();
			Metatag<?, ?> metatag = MetatagHolder.of(registry).specter$getMetatag(metatagId);
			if (metatag == null || metatag.side() != this.side) continue;

			MetatagMap<?, ?> map = metatagMaps.computeIfAbsent(metatag, this::createMap);
			metatagResources.forEach(metatagResource -> map.parseResource(metatagId, metatagResource));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public CompletableFuture<Void> apply(Map<Metatag<?, ?>, MetatagMap<?, ?>> data, ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			data.forEach((metatag, map) -> loadMetatag((Metatag<Object, Object>) metatag, (MetatagMap<Object, Object>) map));

			if (this.side != ResourceType.SERVER_DATA) return;
			MetatagSyncS2CPayload.clearCache();

			MinecraftServer server = SpecterRegistry.getServer();
			if (server == null) return;

			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList())
				MetatagSyncS2CPayload.createPayloads().forEach(payload -> ServerPlayNetworking.send(player, payload));
		}, executor);
	}

	private <R, V> void loadMetatag(Metatag<R, V> metatag, MetatagMap<R, V> map) {
		Registry<R> registry = metatag.registry();

		MetatagValueHolder<R> holder = MetatagValueHolder.getOrCreate(registry);
		if (metatag.side() == this.side)
			holder.specter$clearMetatag(metatag);

		map.getValues().forEach((id, value) -> metatag.put(registry.get(id), value));
	}

	@Override
	public Identifier getFabricId() {
		return Identifier.of(SpecterGlobals.MODID, this.side == ResourceType.SERVER_DATA ? "metatags_data" : "metatags_resources");
	}
}
