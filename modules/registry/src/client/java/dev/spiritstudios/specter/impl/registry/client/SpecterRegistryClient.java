package dev.spiritstudios.specter.impl.registry.client;

import static dev.spiritstudios.specter.impl.registry.SpecterRegistry.METATAGS_SYNC;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagValueHolder;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagsS2CPayload;

public class SpecterRegistryClient implements ClientModInitializer {
	private static <R, V> void putMetatagValues(MetatagsS2CPayload.MetatagData<R, V> data) {
		if (Minecraft.getInstance().hasSingleplayerServer())
			return;

		Metatag<R, V> metatag = data.metatag();
		ResourceKey<Registry<R>> registry = metatag.registryKey();
		MetatagValueHolder<R> metatagHolder = MetatagValueHolder.getOrCreate(registry);

		metatagHolder.specter$clearMetatag(metatag);

		data.entries().forEach((key, value) -> metatagHolder.specter$putMetatagValue(metatag, key, value));
	}

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(
				METATAGS_SYNC.payloadId(),
				(payload, context) ->
						context.client().execute(() ->
								METATAGS_SYNC.receive(payload, context.player().registryAccess()))
		);

		METATAGS_SYNC.receiveCallback().register((payload, registryManager) -> {
			for (MetatagsS2CPayload.MetatagData<?, ?> data : payload.metatags()) {
				putMetatagValues(data);
			}
		});
	}
}
