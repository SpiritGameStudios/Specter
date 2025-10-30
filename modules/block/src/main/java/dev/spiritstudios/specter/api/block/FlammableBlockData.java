package dev.spiritstudios.specter.api.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Range;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * The flammability data of a block.
 *
 * @see BlockMetatags#FLAMMABLE
 */
public record FlammableBlockData(@Range(from = 0, to = Integer.MAX_VALUE) int igniteOdds,
								@Range(from = 0, to = Integer.MAX_VALUE) int burnOdds) {
	public static final Codec<FlammableBlockData> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(
					Codec.intRange(0, Integer.MAX_VALUE).fieldOf("burn").forGetter(FlammableBlockData::igniteOdds),
					Codec.intRange(0, Integer.MAX_VALUE).fieldOf("spread").forGetter(FlammableBlockData::burnOdds)
			).apply(instance, FlammableBlockData::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, FlammableBlockData> PACKET_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT,
			FlammableBlockData::igniteOdds,
			ByteBufCodecs.INT,
			FlammableBlockData::burnOdds,
			FlammableBlockData::new
	);

	public static FlammableBlockData fromEntry(FlammableBlockRegistry.Entry entry) {
		return new FlammableBlockData(entry.getBurnChance(), entry.getSpreadChance());
	}
}
