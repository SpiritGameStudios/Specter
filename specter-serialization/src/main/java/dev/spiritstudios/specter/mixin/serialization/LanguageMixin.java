package dev.spiritstudios.specter.mixin.serialization;

import java.util.function.BiConsumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.serialization.JsonOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Language;

import dev.spiritstudios.specter.impl.serialization.SpecterSerialization;

@Mixin(Language.class)
public class LanguageMixin {
	@WrapOperation(method = "load(Ljava/io/InputStream;Ljava/util/function/BiConsumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/JsonHelper;asString(Lcom/google/gson/JsonElement;Ljava/lang/String;)Ljava/lang/String;"))
	private static String load(JsonElement element, String name, Operation<String> original, @Local(argsOnly = true) BiConsumer<String, String> entryConsumer, @Share("skip") LocalBooleanRef skip) {
		if (element.isJsonPrimitive()) {
			skip.set(false);
			return original.call(element, name);
		}

		Text text = TextCodecs.CODEC.parse(JsonOps.INSTANCE, element).getOrThrow(JsonParseException::new);
		SpecterSerialization.TEXT_TRANSLATIONS_BUILDER.get().put(name, text);

		skip.set(true);
		return "";
	}

	@WrapWithCondition(method = "load(Ljava/io/InputStream;Ljava/util/function/BiConsumer;)V", at = @At(value = "INVOKE", target = "Ljava/util/function/BiConsumer;accept(Ljava/lang/Object;Ljava/lang/Object;)V"))
	private static <T, U> boolean skip(BiConsumer<T, U> instance, T t, U u, @Share("skip") LocalBooleanRef skip) {
		return !skip.get();
	}
}
