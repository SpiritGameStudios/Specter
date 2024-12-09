package dev.spiritstudios.specter.impl.registry;

import com.mojang.serialization.Lifecycle;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.api.registry.reloadable.ClientReloadableRegistryEvents;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagValueHolder;
import dev.spiritstudios.specter.impl.registry.metatag.data.MetatagReloader;
import dev.spiritstudios.specter.impl.registry.metatag.network.MetatagSyncS2CPayload;
import dev.spiritstudios.specter.impl.registry.reloadable.SpecterReloadableRegistriesImpl;
import dev.spiritstudios.specter.impl.registry.reloadable.network.ReloadableRegistrySyncS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SpecterRegistryClient implements ClientModInitializer {
	private static final RegistryEntryInfo DEFAULT_REGISTRY_ENTRY_INFO = new RegistryEntryInfo(Optional.empty(), Lifecycle.experimental());

	private static final List<ReloadableRegistrySyncS2CPayload.Entry<?>> registrySyncEntries = new ArrayList<>();

	@SuppressWarnings("unchecked")
	private static <V> void applyMetatagSync(MetatagSyncS2CPayload<V> payload) {
		if (MinecraftClient.getInstance().isIntegratedServerRunning())
			return;

		Metatag<Object, V> metatag = (Metatag<Object, V>) payload.metatagPair().metatag();
		Registry<Object> registry = metatag.registry();
		MetatagValueHolder<Object> metatagHolder = MetatagValueHolder.getOrCreate(registry);

		metatagHolder.specter$clearMetatag(metatag);

		payload.metatagPair().entries().forEach(entry -> {
			Identifier id = Identifier.of(payload.metatagPair().namespace(), entry.id());
			Object object = registry.get(id);

			if (object == null)
				throw new IllegalStateException("Entry " + id + " is not in the registry");

			V value = entry.value();
			metatagHolder.specter$putMetatagValue(metatag, object, value);
		});
	}

	private static <T> DynamicRegistryManager.Entry<T> createRegistry(ReloadableRegistrySyncS2CPayload.Entry<T> entry) {
		MutableRegistry<T> registry = new SimpleRegistry<>(entry.key(), Lifecycle.experimental());

		entry.entries().forEach((id, element) -> {
			registry.add(
				RegistryKey.of(entry.key(), id),
				element,
				DEFAULT_REGISTRY_ENTRY_INFO
			);
		});

		return new DynamicRegistryManager.Entry<>(entry.key(), registry);
	}

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new MetatagReloader(ResourceType.CLIENT_RESOURCES));

		ClientPlayNetworking.registerGlobalReceiver(MetatagSyncS2CPayload.ID, (payload, context) -> context.client().execute(() -> applyMetatagSync(payload)));

		// Credit to @MerchantPug for the idea of overriding the vanilla manager
		ClientPlayNetworking.registerGlobalReceiver(ReloadableRegistrySyncS2CPayload.ID, (payload, context) -> context.client().execute(() -> {
			ClientPlayNetworkHandler networkHandler = context.client().getNetworkHandler();
			Objects.requireNonNull(networkHandler);

			registrySyncEntries.add(payload.entry());
			if (!payload.finished()) return;

			DynamicRegistryManager.Immutable reloadableManager = new DynamicRegistryManager.ImmutableImpl(
				registrySyncEntries.stream().map(SpecterRegistryClient::createRegistry)
			).toImmutable();
			reloadableManager.streamAllRegistries().forEach(entry -> entry.value().clearTags());
			registrySyncEntries.clear();

			SpecterReloadableRegistriesImpl.setRegistryManager(reloadableManager);
			ClientReloadableRegistryEvents.SYNC_FINISHED.invoker().onSyncFinished(context.client(), reloadableManager);
		}));

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			SpecterReloadableRegistriesImpl.setRegistryManager(null);
			registrySyncEntries.clear();
		});
	}
}
