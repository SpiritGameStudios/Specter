package dev.spiritstudios.specter.impl.config;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import dev.spiritstudios.specter.api.config.Config;
import dev.spiritstudios.specter.api.core.SpecterGlobals;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ValueImpl<T> implements Config.Value<T> {
	private final T defaultValue;
	private final Codec<T> codec;
	private final PacketCodec<ByteBuf, T> packetCodec;
	private final boolean sync;
	private final String comment;
	private final Pair<T, T> range;

	private MapCodec<T> mapCodec;
	private String name;

	private T value;

	public ValueImpl(T defaultValue,
					 Codec<T> codec,
					 PacketCodec<ByteBuf, T> packetCodec,
					 String comment,
					 boolean sync,
					 Pair<T, T> range
	) {
		this.defaultValue = defaultValue;
		this.codec = codec;
		this.comment = comment;
		this.sync = sync;
		this.packetCodec = packetCodec;
		this.range = range;

		this.value = defaultValue;
	}

	@Override
	public T get() {
		return value;
	}

	@Override
	public T defaultValue() {
		return defaultValue;
	}

	@Override
	public void set(T value) {
		this.value = value;
	}

	@Override
	public void init(String name) {
		this.mapCodec = codec.fieldOf(name);
		this.name = name;
	}

	@Override
	public <T1> RecordBuilder<T1> encode(DynamicOps<T1> ops, RecordBuilder<T1> builder) {
		if (mapCodec == null) {
			SpecterGlobals.LOGGER.error("Value not initialized, cannot encode");
			return builder;
		}

		return mapCodec.encode(get(), ops, builder);
	}

	@Override
	public <T1> boolean decode(DynamicOps<T1> ops, T1 input) {
		if (mapCodec == null) {
			SpecterGlobals.LOGGER.error("Value not initialized, cannot decode");
			return false;
		}

		DataResult<T> result = mapCodec.decoder().parse(ops, input);
		if (result.error().isPresent()) {
			SpecterGlobals.LOGGER.error("Failed to decode value: {}", result.error().get());
			return false;
		}

		T value = result.result().orElseThrow();
		this.set(value);

		return true;
	}

	@Override
	public void packetDecode(ByteBuf buf) {
		set(packetCodec.decode(buf));
	}

	@Override
	public void packetEncode(ByteBuf buf) {
		packetCodec.encode(buf, get());
	}

	@Override
	public Optional<String> comment() {
		return Optional.ofNullable(comment);
	}

	@Override
	public boolean sync() {
		return sync;
	}

	@Override
	public Pair<T, T> range() {
		return range;
	}

	@Override
	public String translationKey(Identifier configId) {
		return String.format("config.%s.%s.%s", configId.getNamespace(), configId.getPath(), name);
	}
}
