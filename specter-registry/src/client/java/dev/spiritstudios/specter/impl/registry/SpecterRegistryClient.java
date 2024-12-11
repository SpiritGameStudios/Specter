package dev.spiritstudios.specter.impl.registry;

import com.mojang.serialization.Lifecycle;
import dev.spiritstudios.specter.api.core.SpecterGlobals;
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
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.resource.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SpecterRegistryClient implements ClientModInitializer {
	private static final RegistryEntryInfo DEFAULT_REGISTRY_ENTRY_INFO = new RegistryEntryInfo(Optional.empty(), Lifecycle.experimental());

	private static final List<ReloadableRegistrySyncS2CPayload.Entry<?>> registrySyncEntries = new ArrayList<>();

	private static <R, V> void applyMetatagSync(MetatagSyncS2CPayload<R, V> payload) {
		if (MinecraftClient.getInstance().isIntegratedServerRunning())
			return;

		SpecterGlobals.debug("Received payload for metatag %s".formatted(payload.metatag().id()));

		Metatag<R, V> metatag = payload.metatag();
		Registry<R> registry = metatag.registry();
		MetatagValueHolder<R> metatagHolder = MetatagValueHolder.getOrCreate(registry);

		metatagHolder.specter$clearMetatag(metatag);

		payload.values().forEach(entry -> {
			SpecterGlobals.debug("Put value %s".formatted(entry));
			metatagHolder.specter$putMetatagValue(metatag, entry.getFirst(), entry.getSecond());
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

		// Credit to @MerchantPug for the idea of creating our own DRM
		// https://github.com/GreenhouseModding/reloadable-datapack-registries/blob/1.20.4/common/src/main/java/dev/greenhouseteam/rdpr/impl/network/ReloadRegistriesClientboundPacket.java#L44
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
