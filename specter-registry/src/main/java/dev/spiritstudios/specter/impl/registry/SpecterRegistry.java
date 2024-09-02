package dev.spiritstudios.specter.impl.registry;

import com.mojang.datafixers.util.Pair;
import dev.spiritstudios.specter.api.registry.registration.Registrar;
import dev.spiritstudios.specter.impl.registry.attachment.data.AttachmentReloader;
import dev.spiritstudios.specter.impl.registry.attachment.network.AttachmentSyncS2CPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpecterRegistry implements ModInitializer {
	private static MinecraftServer server;

	@ApiStatus.Internal
	public static MinecraftServer getServer() {
		return server;
	}

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new AttachmentReloader(ResourceType.SERVER_DATA));

		PayloadTypeRegistry.playS2C().register(AttachmentSyncS2CPayload.ID, AttachmentSyncS2CPayload.CODEC);

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
			AttachmentSyncS2CPayload.createPayloads()
				.forEach(sender::sendPacket));

		ServerLifecycleEvents.SERVER_STARTING.register(server -> SpecterRegistry.server = server);
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			if (SpecterRegistry.server == server) SpecterRegistry.server = null;
		});

		List<Pair<String, List<String>>> registrars = FabricLoader.getInstance().getAllMods().stream()
			.map(ModContainer::getMetadata)
			.map((metadata) -> Pair.of(metadata.getId(), metadata.getCustomValue("specter:registrars")))
			.filter((pair) -> Objects.nonNull(pair.getSecond()))
			.map((pair) -> Pair.of(pair.getFirst(), pair.getSecond().getAsArray()))
			.map((pair) -> {
				List<String> list = new ArrayList<>();
				for (CustomValue value : pair.getSecond()) {
					String registrar = value.getAsString();
					if (registrar != null) list.add(registrar);
				}

				return Pair.of(pair.getFirst(), list);
			}).toList();

		ClassLoader classLoader = getClass().getClassLoader();

		registrars.forEach((pair) -> {
			String modid = pair.getFirst();
			List<String> registrarNames = pair.getSecond();

			registrarNames.forEach((registrarName) -> {
				try {
					Class<?> clazz = classLoader.loadClass(registrarName);
					if (!(Registrar.class.isAssignableFrom(clazz)))
						throw new RuntimeException("Registrar " + registrarName + " does not implement Registrar");

					@SuppressWarnings("unchecked") Class<? extends Registrar<Object>> registrarClass = (Class<? extends Registrar<Object>>) clazz;
					Registrar.process(registrarClass, modid);
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException("Failed to register registrar " + registrarName, e);
				}
			});
		});
	}
}

