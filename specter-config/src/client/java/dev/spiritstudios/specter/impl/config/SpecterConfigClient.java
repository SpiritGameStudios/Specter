package dev.spiritstudios.specter.impl.config;

import dev.spiritstudios.specter.api.config.Config;
import dev.spiritstudios.specter.api.config.ConfigManager;
import dev.spiritstudios.specter.api.config.annotations.Sync;
import dev.spiritstudios.specter.api.core.util.ReflectionHelper;
import dev.spiritstudios.specter.impl.config.network.ConfigSyncS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.Map;

public class SpecterConfigClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ConfigManager.reloadConfigs());

		ClientPlayNetworking.registerGlobalReceiver(ConfigSyncS2CPayload.ID, (payload, context) -> {
			Map<Identifier, String> configs = payload.configs();

			context.client().execute(() -> configs.forEach((id, data) -> {
				Config config = ConfigManager.getConfigById(id);
				if (config == null) return;

				config.save();
				Config serverConfig = ConfigManager.GSON.fromJson(data, config.getClass());

				for (Field field : serverConfig.getClass().getDeclaredFields()) {
					if (!field.isAnnotationPresent(Sync.class)) continue;

					ReflectionHelper.setFieldValue(
						config,
						field,
						ReflectionHelper.getFieldValue(serverConfig, field)
					);
				}

				ConfigManager.getConfigs().set(ConfigManager.getConfigs().indexOf(config), config);
			}));
		});
	}
}
