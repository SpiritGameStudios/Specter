package dev.spiritstudios.specter.mixin.serialization.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import dev.spiritstudios.specter.impl.serialization.text.TextContentRegistryImpl;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.TextContent;
import net.minecraft.util.StringIdentifiable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Supplier;
import java.util.stream.Stream;

@Mixin(TextCodecs.class)
public class TextCodecsMixin {
    @ModifyVariable(method = "dispatchingCodec", at = @At("STORE"), ordinal = 0)
    private static <T extends StringIdentifiable, E> MapCodec<E> dispatchingCodec(MapCodec<E> codec, T[] types) {
        if (!types.getClass().getComponentType().isAssignableFrom(TextContent.Type.class)) return codec;

        return new MapCodec<>() {
            @Override
            public <T1> RecordBuilder<T1> encode(E input, DynamicOps<T1> ops, RecordBuilder<T1> prefix) {
                return codec.encode(input, ops, prefix);
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T1> DataResult<E> decode(DynamicOps<T1> ops, MapLike<T1> input) {
                DataResult<E> originalResult = codec.decode(ops, input);
                if (originalResult.result().isPresent()) return originalResult;

                return TextContentRegistryImpl.getTypes().values().stream()
                        .filter(entry -> input.get(entry.field()) != null)
                        .findFirst()
                        .map(entry -> (DataResult<E>) entry.type().codec().decode(ops, input))
                        .orElse(originalResult);
            }

            @Override
            public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
                return Stream.concat(
                        codec.keys(ops),
                        TextContentRegistryImpl.getTypes().values().stream().flatMap(entry -> entry.type().codec().keys(ops))
                );
            }
        };
    }

    @SuppressWarnings("unchecked")
    @WrapOperation(method = "dispatchingCodec", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/StringIdentifiable;createBasicCodec(Ljava/util/function/Supplier;)Lcom/mojang/serialization/Codec;"))
    private static <T extends StringIdentifiable> Codec<T> dispatchingCodec(Supplier<T[]> values, Operation<Codec<T>> original) {
        Codec<T> originalCodec = original.call(values);
        if (!values.get().getClass().getComponentType().isAssignableFrom(TextContent.Type.class)) return originalCodec;
        Codec<T> textContentTypeCodec = Codec.stringResolver(StringIdentifiable::asString, id -> (T) TextContentRegistryImpl.getTypes().get(id).type());

        return new Codec<>() {
            @Override
            public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
                DataResult<T1> originalResult = originalCodec.encode(input, ops, prefix);
                if (originalResult.result().isPresent()) return originalResult;

                return textContentTypeCodec.encode(input, ops, prefix);
            }

            @Override
            public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
                DataResult<Pair<T, T1>> originalResult = originalCodec.decode(ops, input);
                if (originalResult.result().isPresent()) return originalResult;

                return textContentTypeCodec.decode(ops, input);
            }
        };
    }
}
