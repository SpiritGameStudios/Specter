package dev.spiritstudios.specter.impl.registry.metatag.data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagEventsImpl;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagHolder;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagValueHolder;

public class MetatagReloader implements SimpleResourceReloadListener<List<MetatagContent<?, ?>>> {
	private final ResourceType side;

	public MetatagReloader(ResourceType side) {
		this.side = side;
	}

	private <R, V> MetatagContent<R, V> createMap(Metatag<R, V> metatag) {
		return new MetatagContent<>(metatag.registry(), metatag);
	}

	@Override
	public CompletableFuture<List<MetatagContent<?, ?>>> load(ResourceManager manager, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<Metatag<?, ?>, MetatagContent<?, ?>> metatagContents = new Object2ObjectOpenHashMap<>();

			// For each registry
			for (RegistryEntry<? extends Registry<?>> entry : Registries.REGISTRIES.getIndexedEntries()) {
				if (entry.getKey().isEmpty()) continue;

				Identifier registryId = entry.getKey().get().getValue();
				String metatagPath = registryId.getNamespace() + "/" + registryId.getPath();

				Map<Identifier, List<Resource>> resources = ResourceFinder.json("metatags/" + metatagPath).findAllResources(manager);

				if (resources.isEmpty()) continue;

				Registry<?> registry = entry.value();
				for (Map.Entry<Identifier, List<Resource>> resource : resources.entrySet()) {
					Identifier metatagResourceId = resource.getKey();

					// Transform the path into the Metatag ID (e.g. specter:metatags/minecraft/block/strippable.json -> specter:strippable)
					String path = metatagResourceId.getPath();
					path = path.substring(path.lastIndexOf('/') + 1);
					path = path.substring(0, path.lastIndexOf('.'));
					Identifier metatagId = Identifier.of(metatagResourceId.getNamespace(), path);

					Metatag<?, ?> metatag = MetatagHolder.of(registry).specter$getMetatag(metatagId);
					if (metatag == null || metatag.side() != this.side) continue;

					MetatagContent<?, ?> content = metatagContents.computeIfAbsent(metatag, this::createMap);

					resource.getValue()
							.forEach(metatagResource -> content.parseAndAddResource(metatagId, metatagResource));
				}
			}

			return List.copyOf(metatagContents.values());
		}, executor);
	}

	@Override
	public CompletableFuture<Void> apply(List<MetatagContent<?, ?>> data, ResourceManager manager, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			data.forEach((content) -> {
				loadMetatag(content);
				MetatagEventsImpl.getLoadedEvent(content.getMetatag())
						.ifPresent(event -> event.invoker().onMetatagLoaded(manager));
			});
		}, executor);
	}

	private <R, V> void loadMetatag(MetatagContent<R, V> content) {
		Metatag<R, V> metatag = content.getMetatag();
		Registry<R> registry = metatag.registry();

		MetatagValueHolder<R> holder = MetatagValueHolder.getOrCreate(registry);
		if (metatag.side() == this.side)
			holder.specter$clearMetatag(metatag);

		content.getValues().forEach((id, value) -> metatag.put(registry.get(id), value));
	}

	@Override
	public Identifier getFabricId() {
		return Identifier.of(SpecterGlobals.MODID, this.side == ResourceType.SERVER_DATA ? "metatags_data" : "metatags_resources");
	}
}
