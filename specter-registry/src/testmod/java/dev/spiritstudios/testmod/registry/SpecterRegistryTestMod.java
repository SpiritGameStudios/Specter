package dev.spiritstudios.testmod.registry;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.minecraft.block.Block;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.api.registry.reloadable.SpecterReloadableRegistries;
import dev.spiritstudios.specter.impl.core.Specter;

public class SpecterRegistryTestMod implements ModInitializer {
	public static final String MODID = "specter-registry-testmod";
	public static final Identifier METATAG_ID = Identifier.of(MODID, "metatag_test");

	public static final Metatag<Block, Integer> TEST_METATAG = Metatag.builder(
			RegistryKeys.BLOCK,
			METATAG_ID,
			Codec.INT
	).packetCodec(PacketCodecs.INTEGER.cast()).build();

	public static final Identifier CLIENT_METATAG_ID = Identifier.of(MODID, "metatag_client_test");

	public static final Metatag<Block, Integer> TEST_CLIENT_METATAG = Metatag.builder(
			RegistryKeys.BLOCK,
			CLIENT_METATAG_ID,
			Codec.INT
	).side(ResourceType.CLIENT_RESOURCES).build();

	public static final RegistryKey<Registry<Chocolate>> CHOCOLATE_KEY = RegistryKey.ofRegistry(Identifier.of(MODID, "chocolate"));

	@Override
	public void onInitialize() {
		SpecterReloadableRegistries.registerSynced(CHOCOLATE_KEY, Chocolate.CODEC, Chocolate.PACKET_CODEC.cast());

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ChocolateCommand.register(dispatcher, registryAccess);
		});

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
			SpecterReloadableRegistries.lookup()
					.map(manager -> manager.getOrThrow(CHOCOLATE_KEY))
					.ifPresent(wrapperLookup -> {
						wrapperLookup.streamEntries().forEach(entry -> {
							Specter.debug(entry.getIdAsString() + ": " + entry.value().toString());
						});
					});
		});
	}
}
