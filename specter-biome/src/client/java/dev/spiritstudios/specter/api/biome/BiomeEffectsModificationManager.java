package dev.spiritstudios.specter.api.biome;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// TODO: Create the specter-data module to reintroduce the data-driven part of this system
public final class BiomeEffectsModificationManager {
	private static final Map<RegistryKey<Biome>, BiomeEffectsModification> modifications = new Object2ObjectOpenHashMap<>();

	public static void register(RegistryKey<Biome> biome, BiomeEffectsModification modifier) {
		modifications.put(biome, modifier);
	}

	public static void register(List<RegistryKey<Biome>> biomes, BiomeEffectsModification modifier) {
		for (RegistryKey<Biome> biome : biomes) modifications.put(biome, modifier);
	}

	public static void apply(DynamicRegistryManager registryManager) {
		Registry<Biome> biomes = registryManager.get(RegistryKeys.BIOME);

		for (Map.Entry<RegistryKey<Biome>, BiomeEffectsModification> entry : modifications.entrySet()) {
			RegistryKey<Biome> biomeId = entry.getKey();
			BiomeEffectsModification modifier = entry.getValue();
			Optional<Biome> biome = biomes.getOrEmpty(biomeId);

			if (biome.isEmpty()) continue;
			modifier.apply(biome.get());
		}
	}
}
