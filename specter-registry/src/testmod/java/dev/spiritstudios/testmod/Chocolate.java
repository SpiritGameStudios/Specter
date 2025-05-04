package dev.spiritstudios.testmod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

/**
 * thank you pug
 */
public record Chocolate(Type type, NutType nutType, String topping) {
	public static final Codec<Chocolate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.stringResolver(Enum::name, string -> Enum.valueOf(Type.class, string)).fieldOf("type").forGetter(Chocolate::type),
		Codec.stringResolver(Enum::name, string -> Enum.valueOf(NutType.class, string)).fieldOf("nut_type").forGetter(Chocolate::nutType),
		Codec.STRING.optionalFieldOf("topping", "none").forGetter(Chocolate::topping)
	).apply(instance, Chocolate::new));

	public static final PacketCodec<ByteBuf, Chocolate> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_INT.xmap(ordinal -> Type.values()[ordinal], Enum::ordinal),
		Chocolate::type,
		PacketCodecs.VAR_INT.xmap(ordinal -> NutType.values()[ordinal], Enum::ordinal),
		Chocolate::nutType,
		PacketCodecs.string(Integer.MAX_VALUE),
		Chocolate::topping,
		Chocolate::new
	);

	public enum Type {
		MILK,
		DARK,
		WHITE
	}

	public enum NutType {
		NONE("none"),
		ALMOND("almonds"),
		CASHEW("cashews"),
		MACADAMIA("macadamias"),
		PEANUT("peanuts"),
		PISTACHIO("pistachios"),
		DEEZ("deez nuts"),
		PINE("pine nuts"),
		WALNUT("walnuts");

		private final String plural;

		NutType(String plural) {
			this.plural = plural;
		}

		public String plural() {
			return this.plural;
		}
	}
}
