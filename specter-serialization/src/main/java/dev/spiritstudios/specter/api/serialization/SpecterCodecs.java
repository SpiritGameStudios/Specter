package dev.spiritstudios.specter.api.serialization;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import dev.spiritstudios.specter.impl.serialization.codec.FuzzyCodec;
import dev.spiritstudios.specter.impl.serialization.codec.KeyDispatchingCodec;
import net.minecraft.util.Util;
import org.joml.Vector4d;

import java.util.List;
import java.util.function.Function;

public final class SpecterCodecs {
	public static final Codec<Vector4d> VECTOR4D = Codec.DOUBLE.listOf()
		.comapFlatMap(
			list -> Util.decodeFixedLengthList(list, 4)
				.map(vecList -> new Vector4d(vecList.getFirst(), vecList.get(1), vecList.get(2), vecList.get(3))),
			vector -> List.of(vector.x(), vector.y(), vector.z(), vector.w())
		);

	private SpecterCodecs() {
		throw new UnsupportedOperationException("Cannot instantiate utility class");
	}

	public static <T extends Enum<T>> Codec<T> enumCodec(Class<T> clazz) {
		return Codec.STRING.comapFlatMap(
			string -> {
				try {
					return DataResult.success(Enum.valueOf(clazz, string.toUpperCase()));
				} catch (IllegalArgumentException | NullPointerException e) {
					return DataResult.error(() -> "Value \"%s\" invalid for enum \"%s\"".formatted(string, clazz.getSimpleName()));
				}
			},
			t -> t.name().toLowerCase()
		);
	}

	public static Codec<Integer> clampedRange(int min, int max) {
		return Codec.INT.xmap(
			value -> Math.clamp(value, min, max),
			value -> Math.clamp(value, min, max)
		);
	}

	public static Codec<Float> clampedRange(float min, float max) {
		return Codec.FLOAT.xmap(
			value -> Math.clamp(value, min, max),
			value -> Math.clamp(value, min, max)
		);
	}

	public static Codec<Double> clampedRange(double min, double max) {
		return Codec.DOUBLE.xmap(
			value -> Math.clamp(value, min, max),
			value -> Math.clamp(value, min, max)
		);
	}

	public static <T> MapCodec<T> fuzzy(List<MapCodec<? extends T>> codecs, Function<T, MapEncoder<? extends T>> codecGetter) {
		return new FuzzyCodec<>(codecs, codecGetter);
	}

	/**
	 * Creates a codec that changes depending on the existing keys in a decoded map
	 *
	 * @param defaultCodec   The codec to use for encoding
	 * @param possibleCodecs All codecs that can be returned by {@code dispatcher}
	 * @param dispatcher     A function that returns a codec depending on the inputted map. If this returns a value not contained within {@code possibleCodecs}, an error will be returned.
	 */
	public static <T> MapCodec<T> keyDispatching(MapCodec<T> defaultCodec, List<MapCodec<T>> possibleCodecs, Function<MapLike<?>, MapCodec<T>> dispatcher) {
		return new KeyDispatchingCodec<>(defaultCodec, possibleCodecs, dispatcher);
	}
}

