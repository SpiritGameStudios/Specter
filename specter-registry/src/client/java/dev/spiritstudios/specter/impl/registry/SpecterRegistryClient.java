package dev.spiritstudios.specter.impl.registry;

import com.mojang.serialization.Lifecycle;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.api.registry.reloadable.ClientReloadableRegistryEvents;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagValueHolder;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagsS2CPayload;
import dev.spiritstudios.specter.impl.registry.metatag.data.MetatagReloader;
import dev.spiritstudios.specter.impl.registry.reloadable.ReloadableRegistriesS2CPayload;
import dev.spiritstudios.specter.impl.registry.reloadable.SpecterReloadableRegistriesImpl;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.resource.ResourceType;

import java.util.Optional;

import static dev.spiritstudios.specter.impl.registry.SpecterRegistry.METATAGS_SYNC;
import static dev.spiritstudios.specter.impl.registry.SpecterRegistry.RELOADABLE_REGISTRIES_SYNC;

public class SpecterRegistryClient implements ClientModInitializer {
	private static final RegistryEntryInfo DEFAULT_REGISTRY_ENTRY_INFO = new RegistryEntryInfo(Optional.empty(), Lifecycle.experimental());

	private static <R, V> void putMetatagValues(MetatagsS2CPayload.MetatagData<R, V> data) {
		if (MinecraftClient.getInstance().isIntegratedServerRunning())
			return;

		Metatag<R, V> metatag = data.metatag();
		Registry<R> registry = metatag.registry();
		MetatagValueHolder<R> metatagHolder = MetatagValueHolder.getOrCreate(registry);

		metatagHolder.specter$clearMetatag(metatag);

		data.entries().forEach((key, value) -> metatagHolder.specter$putMetatagValue(metatag, key, value));
	}

	private static <T> DynamicRegistryManager.Entry<T> createRegistry(ReloadableRegistriesS2CPayload.RegistryData<T> data) {
		RegistryKey<Registry<T>> key = RegistryKey.ofRegistry(data.key());

		MutableRegistry<T> registry = new SimpleRegistry<>(
			key,
			Lifecycle.experimental()
		);

		data.entries().forEach((id, entry) -> {
			registry.add(
				RegistryKey.of(key, id),
				entry,
				DEFAULT_REGISTRY_ENTRY_INFO
			);
		});

		return new DynamicRegistryManager.Entry<>(key, registry);
	}

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper
			.get(ResourceType.CLIENT_RESOURCES)
			.registerReloadListener(new MetatagReloader(ResourceType.CLIENT_RESOURCES));

		ClientPlayNetworking.registerGlobalReceiver(
			METATAGS_SYNC.payloadId(),
			(payload, context) ->
				context.client().execute(() -> METATAGS_SYNC.receive(payload, context.player().getRegistryManager()))
		);

		ClientPlayNetworking.registerGlobalReceiver(
			RELOADABLE_REGISTRIES_SYNC.payloadId(),
			(payload, context) ->
				context.client().execute(() -> RELOADABLE_REGISTRIES_SYNC.receive(payload, context.player().getRegistryManager()))
		);

		// Credit to @MerchantCalico for the idea of creating our own Registry Manager
		// https://github.com/GreenhouseModding/reloadable-datapack-registries/blob/1.20.4/common/src/main/java/dev/greenhouseteam/rdpr/impl/network/ReloadRegistriesClientboundPacket.java#L44
		RELOADABLE_REGISTRIES_SYNC.receiveCallback().register(payload -> {
			DynamicRegistryManager.Immutable reloadableManager = new DynamicRegistryManager.ImmutableImpl(
				payload.registries()
					.stream()
					.map(SpecterRegistryClient::createRegistry)
			).toImmutable();

			SpecterReloadableRegistriesImpl.setManager(reloadableManager);

			ClientReloadableRegistryEvents.SYNC_FINISHED.invoker().onSyncFinished(
				MinecraftClient.getInstance(),
				reloadableManager
			);
		});

		METATAGS_SYNC.receiveCallback().register(payload -> {
			for (MetatagsS2CPayload.MetatagData<?, ?> data : payload.metatags())
				putMetatagValues(data);
		});


		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			SpecterReloadableRegistriesImpl.setManager(null);
		});
	}
}
