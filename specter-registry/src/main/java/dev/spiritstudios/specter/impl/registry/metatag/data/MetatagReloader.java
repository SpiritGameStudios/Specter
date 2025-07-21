package dev.spiritstudios.specter.impl.registry.metatag.data;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.core.Specter;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagEventsImpl;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagHolder;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagValueHolder;

public class MetatagReloader implements SimpleResourceReloadListener<Collection<MetatagContent<?, ?>>> {
	public static final Identifier ID = Specter.id("metatags_data");

	private final RegistryWrapper.WrapperLookup wrapperLookup;

	public MetatagReloader(RegistryWrapper.WrapperLookup wrapperLookup) {
		this.wrapperLookup = wrapperLookup;
	}

	private <R, V> MetatagContent<R, V> createMap(Metatag<R, V> metatag) {
		return new MetatagContent<>(metatag.registryKey(), metatag);
	}

	@Override
	public CompletableFuture<Collection<MetatagContent<?, ?>>> load(ResourceManager manager, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<Metatag<?, ?>, MetatagContent<?, ?>> metatagContents = new IdentityHashMap<>();

			wrapperLookup.streamAllRegistryKeys().forEach(key -> {
				Identifier registryId = key.getValue();
				String metatagPath = registryId.getNamespace() + "/" + registryId.getPath();

				Map<Identifier, List<Resource>> resources = ResourceFinder
						.json("metatags/" + metatagPath)
						.findAllResources(manager);

				if (resources.isEmpty()) return;

				for (Map.Entry<Identifier, List<Resource>> resource : resources.entrySet()) {
					Identifier resourceId = resource.getKey();

					// Transform the path into the Metatag ID (e.g. specter:metatags/minecraft/block/strippable.json -> specter:strippable)
					String path = resourceId.getPath();
					path = path.substring(path.lastIndexOf('/') + 1);
					path = path.substring(0, path.lastIndexOf('.'));
					Identifier metatagId = Identifier.of(resourceId.getNamespace(), path);

					Metatag<?, ?> metatag = MetatagHolder.ofAny(key).specter$getMetatag(metatagId);
					if (metatag == null) continue;

					MetatagContent<?, ?> content = metatagContents.computeIfAbsent(metatag, this::createMap);

					for (Resource metatagResource : resource.getValue()) {
						content.parseAndAddResource(wrapperLookup, metatagId, metatagResource);
					}
				}
			});

			return metatagContents.values();
		}, executor);
	}

	@Override
	public CompletableFuture<Void> apply(Collection<MetatagContent<?, ?>> data, ResourceManager manager, Executor executor) {
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
		RegistryKey<Registry<R>> registryKey = metatag.registryKey();
		MetatagValueHolder<R> holder = MetatagValueHolder.getOrCreate(registryKey);

		holder.specter$clearMetatag(metatag);

		content.getValues().forEach(pair -> {
			holder.specter$putMetatagValue(metatag, pair.getFirst(), pair.getSecond());
		});
	}

	@Override
	public Identifier getFabricId() {
		return ID;
	}
}
