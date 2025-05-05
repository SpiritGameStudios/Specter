package dev.spiritstudios.specter.api.render.client;

import java.util.function.IntFunction;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public enum BlockRenderLayer implements StringIdentifiable {
	TRIPWIRE(0, RenderLayer::getTripwire, "tripwire"),
	CUTOUT_MIPPED(1, RenderLayer::getCutoutMipped, "cutout_mipped"),
	CUTOUT(2, RenderLayer::getCutout, "cutout"),
	TRANSLUCENT(3, RenderLayer::getTranslucent, "translucent"),
	SOLID(4, RenderLayer::getSolid, "solid");
	public static final Codec<BlockRenderLayer> CODEC = StringIdentifiable.createCodec(BlockRenderLayer::values);
	private static final IntFunction<BlockRenderLayer> BY_ID = ValueLists.createIdToValueFunction(BlockRenderLayer::getId, values(), ValueLists.OutOfBoundsHandling.ZERO);
	public static final PacketCodec<ByteBuf, BlockRenderLayer> PACKET_CODEC = PacketCodecs.indexed(BY_ID, BlockRenderLayer::getId);

	private final Supplier<RenderLayer> layer;
	private final String name;
	private final int id;

	BlockRenderLayer(int id, Supplier<RenderLayer> layer, String name) {
		this.layer = layer;
		this.name = name;
		this.id = id;
	}

	public RenderLayer getLayer() {
		return layer.get();
	}

	@Override
	public String asString() {
		return name;
	}

	public int getId() {
		return id;
	}
}
