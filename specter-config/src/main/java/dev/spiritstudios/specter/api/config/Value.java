package dev.spiritstudios.specter.api.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.config.gui.GuiHint;
import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.core.reflect.ReflectionHelper;
import dev.spiritstudios.specter.api.serialization.CommentedCodec;

/**
 * A config value.
 *
 * @param <T> The type of the value.
 */
public class Value<T> {
	private final T defaultValue;
	private final Codec<T> codec;
	private final @Nullable PacketCodec<RegistryByteBuf, T> packetCodec;
	private final boolean sync;
	private final @Nullable String comment;
	private final Map<Class<?>, Constraint<T>> constraints;
	private final Map<Class<?>, GuiHint<T>> guiHints;

	private @Nullable T override;
	private T value;

	protected Value(
			T defaultValue,
			Codec<T> codec,
			@Nullable PacketCodec<RegistryByteBuf, T> packetCodec,
			@Nullable String comment,
			boolean sync,
			Map<Class<?>, Constraint<T>> constraints, Map<Class<?>, GuiHint<T>> guiHints
	) {
		this.defaultValue = defaultValue;
		this.comment = comment;
		this.sync = sync;
		this.packetCodec = packetCodec;
		this.constraints = Collections.unmodifiableMap(constraints);
		this.guiHints = Collections.unmodifiableMap(guiHints);

		this.codec = new CommentedCodec<>(codec, comment);
		this.value = defaultValue;
	}

	public static String translationKey(String key, Identifier configId) {
		return String.format("config.%s.%s", configId.toTranslationKey(), key);
	}

	public static String translationKey(String key, String configId) {
		return String.format("config.%s.%s", configId, key);
	}

	public DataResult<T> checkConstraints(T value) {
		List<String> errors = new ArrayList<>();

		for (Constraint<T> constraint : constraints.values()) {
			DataResult<T> newResult = constraint.test(value);
			newResult.ifError(error -> errors.add(error.message()));
		}

		if (errors.isEmpty()) return DataResult.success(value);
		return DataResult.error(() -> String.join("\n", errors));
	}

	public T get() {
		return override != null ? override : value;
	}

	public T defaultValue() {
		return defaultValue;
	}

	public void set(T value) {
		DataResult<T> result = checkConstraints(value);
		if (result.isError()) {
			result.ifError(error ->
					SpecterGlobals.LOGGER.error(error.message()));

			return;
		}

		this.value = value;
	}

	/**
	 * Override the current value with a value that will not be saved.
	 * This should generally only be used on client sided values, as when
	 * syncing, this is used to store the server side value.
	 * <p>
	 * On the client, this value will be cleared when the player leaves a world.
	 */
	public void override(@Nullable T value) {
		this.override = value;
	}

	public Codec<T> codec() {
		return codec;
	}

	public Optional<PacketCodec<RegistryByteBuf, T>> packetCodec() {
		return Optional.ofNullable(packetCodec);
	}

	public Optional<String> comment() {
		return Optional.ofNullable(comment);
	}

	public boolean sync() {
		return sync;
	}

	public <C extends Constraint<T>> Optional<C> constraint(Class<C> clazz) {
		Constraint<T> constraint = constraints.get(clazz);
		if (constraint == null) return Optional.empty();
		return ReflectionHelper.cast(constraint, clazz);
	}

	public <H extends GuiHint<T>> Optional<H> hint(Class<H> clazz) {
		GuiHint<T> guiHint = guiHints.get(clazz);
		if (guiHint == null) return Optional.empty();
		return ReflectionHelper.cast(guiHint, clazz);
	}

	// this is checked by the isAssignableFrom, javac is just dumb
	@SuppressWarnings("unchecked")
	public <T1> Optional<Value<T1>> cast(Class<T1> clazz) {
		return clazz.isAssignableFrom(defaultValue.getClass()) ?
				Optional.of((Value<T1>) this) :
				Optional.empty();
	}

	public static class Builder<T> {
		protected final T defaultValue;
		protected final Codec<T> codec;
		protected String comment;
		protected boolean sync;
		protected PacketCodec<RegistryByteBuf, T> packetCodec;
		protected Map<Class<?>, Constraint<T>> constraints = new Object2ObjectOpenHashMap<>();
		protected Map<Class<?>, GuiHint<T>> guiHints = new Object2ObjectOpenHashMap<>();

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

		public Builder<T> packetCodec(PacketCodec<RegistryByteBuf, T> packetCodec) {
			this.packetCodec = packetCodec;
			return this;
		}

		public Builder<T> constrain(Constraint<T> constraint) {
			constraints.put(constraint.getClass(), constraint);
			return this;
		}

		public Builder<T> guiHint(GuiHint<T> guiHint) {
			guiHints.put(guiHint.getClass(), guiHint);
			return this;
		}

		public Value<T> build() {
			return new Value<>(defaultValue, codec, packetCodec, comment, sync, constraints, guiHints);
		}
	}
}
