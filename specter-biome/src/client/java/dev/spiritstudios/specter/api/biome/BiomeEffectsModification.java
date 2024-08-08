package dev.spiritstudios.specter.api.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.specter.api.base.util.ReflectionHelper;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeParticleConfig;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public record BiomeEffectsModification(
	Optional<Integer> fogColor,
	Optional<Integer> waterColor,
	Optional<Integer> waterFogColor,
	Optional<Integer> skyColor,
	Optional<Integer> foliageColor,
	Optional<Integer> grassColor,
	Optional<BiomeEffects.GrassColorModifier> grassColorModifier,
	Optional<BiomeParticleConfig> particleConfig,
	Optional<RegistryEntry<SoundEvent>> loopSound,
	Optional<BiomeMoodSound> moodSound,
	Optional<BiomeAdditionsSound> additionsSound,
	Optional<MusicSound> music,
	List<RegistryKey<Biome>> targets
) {
	public static final Codec<BiomeEffectsModification> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.optionalFieldOf("fog_color").forGetter(BiomeEffectsModification::fogColor),
		Codec.INT.optionalFieldOf("water_color").forGetter(BiomeEffectsModification::waterColor),
		Codec.INT.optionalFieldOf("water_fog_color").forGetter(BiomeEffectsModification::waterFogColor),
		Codec.INT.optionalFieldOf("sky_color").forGetter(BiomeEffectsModification::skyColor),
		Codec.INT.optionalFieldOf("foliage_color").forGetter(BiomeEffectsModification::foliageColor),
		Codec.INT.optionalFieldOf("grass_color").forGetter(BiomeEffectsModification::grassColor),
		BiomeEffects.GrassColorModifier.CODEC.optionalFieldOf("grass_color_modifier").forGetter(BiomeEffectsModification::grassColorModifier),
		BiomeParticleConfig.CODEC.optionalFieldOf("particle_config").forGetter(BiomeEffectsModification::particleConfig),
		SoundEvent.ENTRY_CODEC.optionalFieldOf("ambient_sound").forGetter(BiomeEffectsModification::loopSound),
		BiomeMoodSound.CODEC.optionalFieldOf("mood_sound").forGetter(BiomeEffectsModification::moodSound),
		BiomeAdditionsSound.CODEC.optionalFieldOf("additions_sound").forGetter(BiomeEffectsModification::additionsSound),
		MusicSound.CODEC.optionalFieldOf("music").forGetter(BiomeEffectsModification::music),
		RegistryKey.createCodec(RegistryKeys.BIOME).listOf().fieldOf("targets").forGetter(BiomeEffectsModification::targets)
	).apply(instance, BiomeEffectsModification::new));

	/**
	 * Applies this modifier to the given biome
	 *
	 * @param biome The biome to apply the effects to
	 */
	public void apply(Biome biome) {
		BiomeEffects effects = biome.getEffects();
		BiomeEffects.Builder builder = new BiomeEffects.Builder();

		builder.fogColor(fogColor.orElseGet(effects::getFogColor));
		builder.waterColor(waterColor.orElseGet(effects::getWaterColor));
		builder.waterFogColor(waterFogColor.orElseGet(effects::getWaterFogColor));
		builder.skyColor(skyColor.orElseGet(effects::getSkyColor));

		this.foliageColor.or(effects::getFoliageColor).ifPresent(builder::foliageColor);
		this.grassColor.or(effects::getGrassColor).ifPresent(builder::grassColor);

		builder.grassColorModifier(grassColorModifier.orElseGet(effects::getGrassColorModifier));

		this.particleConfig.or(effects::getParticleConfig).ifPresent(builder::particleConfig);
		this.loopSound.or(effects::getLoopSound).ifPresent(builder::loopSound);
		this.moodSound.or(effects::getMoodSound).ifPresent(builder::moodSound);
		this.additionsSound.or(effects::getAdditionsSound).ifPresent(builder::additionsSound);
		this.music.or(effects::getMusic).ifPresent(builder::music);

		ReflectionHelper.setFieldValue(biome, effectsField, builder.build());
	}

	public static Field effectsField;

	static {
		for (Field field : Biome.class.getDeclaredFields()) {
			if (field.getType() == BiomeEffects.class) {
				effectsField = field;
				effectsField.setAccessible(true);
				break;
			}
		}
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static class Builder {
		private Optional<Integer> fogColor = Optional.empty();
		private Optional<Integer> waterColor = Optional.empty();
		private Optional<Integer> waterFogColor = Optional.empty();
		private Optional<Integer> skyColor = Optional.empty();
		private Optional<Integer> foliageColor = Optional.empty();
		private Optional<Integer> grassColor = Optional.empty();
		private Optional<BiomeEffects.GrassColorModifier> grassColorModifier = Optional.empty();
		private Optional<BiomeParticleConfig> particleConfig = Optional.empty();
		private Optional<RegistryEntry<SoundEvent>> loopSound = Optional.empty();
		private Optional<BiomeMoodSound> moodSound = Optional.empty();
		private Optional<BiomeAdditionsSound> additionsSound = Optional.empty();
		private Optional<MusicSound> music = Optional.empty();

		public Builder fogColor(int fogColor) {
			this.fogColor = Optional.of(fogColor);
			return this;
		}

		public Builder waterColor(int waterColor) {
			this.waterColor = Optional.of(waterColor);
			return this;
		}

		public Builder waterFogColor(int waterFogColor) {
			this.waterFogColor = Optional.of(waterFogColor);
			return this;
		}

		public Builder skyColor(int skyColor) {
			this.skyColor = Optional.of(skyColor);
			return this;
		}

		public Builder foliageColor(int foliageColor) {
			this.foliageColor = Optional.of(foliageColor);
			return this;
		}

		public Builder grassColor(int grassColor) {
			this.grassColor = Optional.of(grassColor);
			return this;
		}

		public Builder grassColorModifier(BiomeEffects.GrassColorModifier grassColorModifier) {
			this.grassColorModifier = Optional.of(grassColorModifier);
			return this;
		}

		public Builder particleConfig(BiomeParticleConfig particleConfig) {
			this.particleConfig = Optional.of(particleConfig);
			return this;
		}

		public Builder loopSound(RegistryEntry<SoundEvent> loopSound) {
			this.loopSound = Optional.of(loopSound);
			return this;
		}

		public Builder moodSound(BiomeMoodSound moodSound) {
			this.moodSound = Optional.of(moodSound);
			return this;
		}

		public Builder additionsSound(BiomeAdditionsSound additionsSound) {
			this.additionsSound = Optional.of(additionsSound);
			return this;
		}

		public Builder music(MusicSound music) {
			this.music = Optional.of(music);
			return this;
		}


		public BiomeEffectsModification build(List<RegistryKey<Biome>> targets) {
			if (targets.isEmpty())
				throw new IllegalArgumentException("Biome effects modification must have at least one target biome");

			return new BiomeEffectsModification(
				fogColor,
				waterColor,
				waterFogColor,
				skyColor,
				foliageColor,
				grassColor,
				grassColorModifier,
				particleConfig,
				loopSound,
				moodSound,
				additionsSound,
				music,
				targets
			);
		}
	}
}
