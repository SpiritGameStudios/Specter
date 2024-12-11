package dev.spiritstudios.specter.api.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import dev.spiritstudios.specter.api.core.reflect.ReflectionHelper;
import dev.spiritstudios.specter.impl.config.NestedConfigValue;
import dev.spiritstudios.specter.impl.config.ValueImpl;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;

/**
 * A config value.
 *
 * @param <T> The type of the value.
 */
public interface Value<T> {
	T get();

	T defaultValue();

	void set(T value);

	default void reset() {
		set(defaultValue());
	}

	@ApiStatus.Internal
	void init(String name);

	<T1> RecordBuilder<T1> encode(DynamicOps<T1> ops, RecordBuilder<T1> builder);

	<T1> boolean decode(DynamicOps<T1> ops, T1 input);

	void packetDecode(ByteBuf buf);

	void packetEncode(ByteBuf buf);

	Optional<String> comment();

	boolean sync();

	String translationKey(String configId);

	default String translationKey(Identifier configId) {
		return translationKey(configId.toTranslationKey());
	}

	String name();

	class Builder<T> {
		protected final T defaultValue;
		protected final Codec<T> codec;
		protected String comment;
		protected boolean sync;
		protected PacketCodec<ByteBuf, T> packetCodec;

		public Builder(T defaultValue, Codec<T> codec) {
			this.defaultValue = defaultValue;
			this.codec = codec;
		}

		public Builder<T> comment(String comment) {
			this.comment = comment;
			return this;
		}

		public Builder<T> sync() {
			if (packetCodec == null) throw new IllegalStateException("Packet codec must be set to enable syncing");
			this.sync = true;
			return this;
		}

		public Builder<T> packetCodec(PacketCodec<ByteBuf, T> packetCodec) {
			this.packetCodec = packetCodec;
			return this;
		}

		public Builder<List<T>> toList() {
			return new Builder<>(List.of(defaultValue), Codec.list(codec));
		}

		public Value<T> build() {
			return new ValueImpl<>(defaultValue, codec, packetCodec, comment, sync);
		}
	}

	class NestedBuilder<T extends NestedConfig<T>> {
		protected final T defaultValue;
		protected String comment;
		protected boolean sync;

		public NestedBuilder(Class<T> clazz) {
			defaultValue = ReflectionHelper.instantiate(clazz);
		}

		public NestedBuilder<T> comment(String comment) {
			this.comment = comment;
			return this;
		}

		public NestedBuilder<T> sync() {
			this.sync = true;
			return this;
		}

		public Value<T> build() {
			return new NestedConfigValue<>(defaultValue, sync, comment);
		}
	}
}
