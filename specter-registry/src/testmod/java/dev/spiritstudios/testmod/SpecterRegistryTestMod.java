package dev.spiritstudios.testmod;

import com.mojang.serialization.Codec;
import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.registry.RegistryHelper;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.api.registry.reloadable.SpecterReloadableRegistries;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.Block;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class SpecterRegistryTestMod implements ModInitializer {
	public static final String MODID = "specter-registry-testmod";
	public static final Identifier METATAG_ID = Identifier.of(MODID, "metatag_test");
	public static final Metatag<Block, Integer> TEST_METATAG = Metatag.builder(
		Registries.BLOCK,
		METATAG_ID,
		Codec.INT,
		PacketCodecs.INTEGER.cast()
	).build();

	public static final Identifier CLIENT_METATAG_ID = Identifier.of(MODID, "metatag_client_test");

	public static final Metatag<Block, Integer> TEST_CLIENT_METATAG = Metatag.builder(
		Registries.BLOCK,
		CLIENT_METATAG_ID,
		Codec.INT,
		PacketCodecs.INTEGER.cast()
	).side(ResourceType.CLIENT_RESOURCES).build();

	public static final RegistryKey<Registry<Chocolate>> CHOCOLATE_KEY = RegistryKey.ofRegistry(Identifier.of(MODID, "chocolate"));

	@Override
	public void onInitialize() {
		RegistryHelper.registerBlocks(SpecterRegistryTestBlocks.class, MODID);
		SpecterReloadableRegistries.registerSynced(CHOCOLATE_KEY, Chocolate.CODEC, Chocolate.PACKET_CODEC.cast());

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ChocolateCommand.register(dispatcher, registryAccess);
		});

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
			SpecterReloadableRegistries.reloadableManager().flatMap(manager -> manager.getOptionalWrapper(CHOCOLATE_KEY)).ifPresent(wrapperLookup -> {
				wrapperLookup.streamEntries().forEach(entry -> {
					SpecterGlobals.debug(entry.getIdAsString() + ": " + entry.value().toString());
				});
			});
		});
	}
}
