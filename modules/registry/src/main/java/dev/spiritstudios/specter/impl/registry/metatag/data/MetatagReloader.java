package dev.spiritstudios.specter.impl.registry.metatag.data;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.core.Specter;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagEventsImpl;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagHolder;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagValueHolder;

public class MetatagReloader implements SimpleResourceReloadListener<Collection<MetatagContent<?, ?>>> {
	public static final ResourceLocation ID = Specter.id("metatags_data");

	private final HolderLookup.Provider wrapperLookup;

	public MetatagReloader(HolderLookup.Provider wrapperLookup) {
		this.wrapperLookup = wrapperLookup;
	}

	private <R, V> MetatagContent<R, V> createMap(Metatag<R, V> metatag) {
		return new MetatagContent<>(metatag.registryKey(), metatag);
	}

	@Override
	public CompletableFuture<Collection<MetatagContent<?, ?>>> load(ResourceManager manager, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<Metatag<?, ?>, MetatagContent<?, ?>> metatagContents = new IdentityHashMap<>();

			wrapperLookup.listRegistryKeys().forEach(key -> {
				ResourceLocation registryId = key.location();
				String metatagPath = registryId.getNamespace() + "/" + registryId.getPath();

				Map<ResourceLocation, List<Resource>> resources = FileToIdConverter
						.json("metatags/" + metatagPath)
						.listMatchingResourceStacks(manager);

				if (resources.isEmpty()) return;

				for (Map.Entry<ResourceLocation, List<Resource>> resource : resources.entrySet()) {
					ResourceLocation resourceId = resource.getKey();

					// Transform the path into the Metatag ID (e.g. specter:metatags/minecraft/block/strippable.json -> specter:strippable)
					String path = resourceId.getPath();
					path = path.substring(path.lastIndexOf('/') + 1);
					path = path.substring(0, path.lastIndexOf('.'));
					ResourceLocation metatagId = ResourceLocation.fromNamespaceAndPath(resourceId.getNamespace(), path);

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
		ResourceKey<Registry<R>> registryKey = metatag.registryKey();
		MetatagValueHolder<R> holder = MetatagValueHolder.getOrCreate(registryKey);

		holder.specter$clearMetatag(metatag);

		content.getValues().forEach(pair -> {
			holder.specter$putMetatagValue(metatag, pair.getFirst(), pair.getSecond());
		});
	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}
}
