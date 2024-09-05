package dev.spiritstudios.specter.impl.registry;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagHolder;
import dev.spiritstudios.specter.impl.registry.metatag.data.MetatagReloader;
import dev.spiritstudios.specter.impl.registry.metatag.network.MetatagSyncS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class SpecterRegistryClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new MetatagReloader(ResourceType.CLIENT_RESOURCES));

		ClientPlayNetworking.registerGlobalReceiver(MetatagSyncS2CPayload.ID, (payload, context) -> context.client().execute(() -> applyMetatagSync(payload)));
	}

	@SuppressWarnings("unchecked")
	private static <V> void applyMetatagSync(MetatagSyncS2CPayload<V> payload) {
		if (MinecraftClient.getInstance().isIntegratedServerRunning())
			return;

		Metatag<Object, V> metatag = (Metatag<Object, V>) payload.metatagPair().metatag();
		Registry<Object> registry = metatag.getRegistry();
		MetatagHolder<Object> metatagHolder = MetatagHolder.of(registry);

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
}
