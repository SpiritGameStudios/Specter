package dev.spiritstudios.specter.impl.registry.client;

import static dev.spiritstudios.specter.impl.registry.SpecterRegistry.METATAGS_SYNC;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagValueHolder;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagsS2CPayload;


public class SpecterRegistryClient implements ClientModInitializer {
	private static <R, V> void putMetatagValues(MetatagsS2CPayload.MetatagData<R, V> data, DynamicRegistryManager registryManager) {
		if (MinecraftClient.getInstance().isIntegratedServerRunning())
			return;

		Metatag<R, V> metatag = data.metatag();
		RegistryKey<Registry<R>> registry = metatag.registryKey();
		MetatagValueHolder<R> metatagHolder = MetatagValueHolder.getOrCreate(registry);

		metatagHolder.specter$clearMetatag(metatag);

		data.entries().forEach((entry, value) -> {
			if (!(entry instanceof RegistryEntry.Reference<R> reference)) return;
			metatagHolder.specter$putMetatagValue(metatag, reference, value);
		});
	}

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(
				METATAGS_SYNC.payloadId(),
				(payload, context) ->
						context.client().execute(() ->
								METATAGS_SYNC.receive(payload, context.player().getRegistryManager()))
		);

		METATAGS_SYNC.receiveCallback().register((payload, registryManager) -> {
			for (MetatagsS2CPayload.MetatagData<?, ?> data : payload.metatags()) {
				putMetatagValues(data, registryManager);
			}
		});
	}
}
