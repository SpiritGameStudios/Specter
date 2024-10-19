package dev.spiritstudios.specter.impl.config;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.RecordBuilder;
import dev.spiritstudios.specter.api.config.NestedConfig;
import dev.spiritstudios.specter.api.config.Value;
import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.core.util.ReflectionHelper;
import dev.spiritstudios.specter.api.serialization.CommentedCodec;
import io.netty.buffer.ByteBuf;

import java.util.Optional;

public class NestedConfigValue<T extends NestedConfig<T>> implements Value<T> {
	private final T defaultValue;
	private final boolean sync;
	private final String comment;
	private T value;
	private MapCodec<T> mapCodec;
	private String name;

	public NestedConfigValue(T defaultValue, boolean sync, String comment) {
		this.defaultValue = defaultValue;
		this.defaultValue.getValueFields().forEach(field -> {
			Value<?> value = ReflectionHelper.getFieldValue(this.defaultValue, field);
			if (value == null) return;

			value.init(field.getName());
			SpecterGlobals.debug("Registered config value: %s".formatted(value.name()));
		});

		this.sync = sync;
		this.comment = comment;
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
		this.name = name;
		this.mapCodec = (comment().isPresent() ? new CommentedCodec<>(defaultValue, comment) : defaultValue).fieldOf(name);
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
		value.packetCodec().decode(buf);
	}

	@Override
	public void packetEncode(ByteBuf buf) {
		value.packetCodec().encode(buf, value);
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
	public String translationKey(String configId) {
		return String.format("config.%s.%s", configId, name);
	}

	@Override
	public String name() {
		return name;
	}
}
