package dev.spiritstudios.specter.mixin.serialization;

import java.util.function.Supplier;
import java.util.stream.Stream;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.text.TextCodecs;
import net.minecraft.text.TextContent;
import net.minecraft.util.StringIdentifiable;

import dev.spiritstudios.specter.impl.serialization.text.TextContentRegistryImpl;

@Mixin(TextCodecs.class)
public class TextCodecsMixin {
	@ModifyVariable(method = "dispatchingCodec", at = @At("STORE"), ordinal = 0)
	private static <T extends StringIdentifiable, E> MapCodec<E> dispatchingCodec(MapCodec<E> original, T[] types) {
		if (!types.getClass().getComponentType().isAssignableFrom(TextContent.Type.class)) return original;

		return new MapCodec<>() {
			@Override
			public <T1> RecordBuilder<T1> encode(E input, DynamicOps<T1> ops, RecordBuilder<T1> prefix) {
				return original.encode(input, ops, prefix);
			}

			@SuppressWarnings("unchecked")
			@Override
			public <T1> DataResult<E> decode(DynamicOps<T1> ops, MapLike<T1> input) {
				DataResult<E> originalResult = original.decode(ops, input);
				return originalResult.result().isPresent() ? originalResult : TextContentRegistryImpl.getTypes().values().stream()
					.filter(entry -> input.get(entry.field()) != null)
					.findFirst()
					.map(entry -> (DataResult<E>) entry.type().codec().decode(ops, input))
					.orElse(originalResult);
			}

			@Override
			public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
				return Stream.concat(
					original.keys(ops),
					TextContentRegistryImpl.getTypes().values().stream()
						.flatMap(entry -> entry.type().codec().keys(ops))
				);
			}
		};
	}

	@SuppressWarnings("unchecked")
	@WrapOperation(method = "dispatchingCodec", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/StringIdentifiable;createBasicCodec(Ljava/util/function/Supplier;)Lcom/mojang/serialization/Codec;"))
	private static <T extends StringIdentifiable> Codec<T> dispatchingCodec(Supplier<T[]> values, Operation<Codec<T>> original) {
		Codec<T> originalCodec = original.call(values);
		if (!values.get().getClass().getComponentType().isAssignableFrom(TextContent.Type.class)) return originalCodec;

		return Codec.withAlternative(
			originalCodec,
			Codec.stringResolver(StringIdentifiable::asString, id -> (T) TextContentRegistryImpl.getTypes().get(id).type())
		);
	}
}
