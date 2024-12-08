package dev.spiritstudios.specter.impl.registry;

import com.mojang.serialization.Lifecycle;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagValueHolder;
import dev.spiritstudios.specter.impl.registry.metatag.data.MetatagReloader;
import dev.spiritstudios.specter.impl.registry.metatag.network.MetatagSyncS2CPayload;
import dev.spiritstudios.specter.impl.registry.reloadable.SpecterReloadableRegistriesImpl;
import dev.spiritstudios.specter.impl.registry.reloadable.network.ReloadableRegistriesSyncS2CPayload;
import dev.spiritstudios.specter.mixin.registry.client.ClientPlayNetworkHandlerAccessor;
import dev.spiritstudios.specter.mixin.registry.client.WorldAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class SpecterRegistryClient implements ClientModInitializer {
	private static final RegistryEntryInfo DEFAULT_REGISTRY_ENTRY_INFO = new RegistryEntryInfo(Optional.empty(), Lifecycle.experimental());

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

	private static <T> DynamicRegistryManager.Entry<T> createRegistry(ReloadableRegistriesSyncS2CPayload.Entry<T> entry) {
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
		ClientPlayNetworking.registerGlobalReceiver(ReloadableRegistriesSyncS2CPayload.ID, (payload, context) -> context.client().execute(() -> {
			ClientPlayNetworkHandler networkHandler = context.client().getNetworkHandler();
			Objects.requireNonNull(networkHandler);

			DynamicRegistryManager.Immutable newManager = new DynamicRegistryManager.ImmutableImpl(
				Stream.concat(
					networkHandler.getRegistryManager().streamAllRegistries()
						.filter(entry -> !SpecterReloadableRegistriesImpl.syncingCodecs().containsKey(entry.key())),
					payload.entries().stream().map(SpecterRegistryClient::createRegistry)
				)
			).toImmutable();
			newManager.streamAllRegistries().forEach(entry -> entry.value().clearTags());
			((ClientPlayNetworkHandlerAccessor) networkHandler).setCombinedDynamicRegistries(newManager);

			World world = context.client().world;
			if (world == null) return;
			((WorldAccessor) world).setRegistryManager(newManager);
		}));
	}
}
