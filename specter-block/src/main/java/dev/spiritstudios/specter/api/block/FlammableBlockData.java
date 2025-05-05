package dev.spiritstudios.specter.api.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Range;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;

/**
 * The flammability data of a block.
 */
public record FlammableBlockData(@Range(from = 0, to = Integer.MAX_VALUE) int burn,
								@Range(from = 0, to = Integer.MAX_VALUE) int spread) {
	public static final Codec<FlammableBlockData> CODEC = RecordCodecBuilder.create(instance -> instance
		.group(
			Codec.intRange(0, Integer.MAX_VALUE).fieldOf("burn").forGetter(FlammableBlockData::burn),
			Codec.intRange(0, Integer.MAX_VALUE).fieldOf("spread").forGetter(FlammableBlockData::spread)
		).apply(instance, FlammableBlockData::new)
	);

	public static final PacketCodec<RegistryByteBuf, FlammableBlockData> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.INTEGER,
		FlammableBlockData::burn,
		PacketCodecs.INTEGER,
		FlammableBlockData::spread,
		FlammableBlockData::new
	);

	public static FlammableBlockData fromEntry(FlammableBlockRegistry.Entry entry) {
		return new FlammableBlockData(entry.getBurnChance(), entry.getSpreadChance());
	}
}
