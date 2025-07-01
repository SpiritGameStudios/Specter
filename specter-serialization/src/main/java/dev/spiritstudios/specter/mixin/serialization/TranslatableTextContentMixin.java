package dev.spiritstudios.specter.mixin.serialization;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Language;

import dev.spiritstudios.specter.impl.serialization.SpecterSerialization;
import dev.spiritstudios.specter.impl.serialization.text.TextTranslationSupplier;

@Mixin(TranslatableTextContent.class)
public abstract class TranslatableTextContentMixin {
	@Shadow
	@Final
	private String key;

	@Shadow
	private List<StringVisitable> translations;

	@Inject(
			method = {
					"visit(Lnet/minecraft/text/StringVisitable$StyledVisitor;Lnet/minecraft/text/Style;)Ljava/util/Optional;",
					"visit(Lnet/minecraft/text/StringVisitable$Visitor;)Ljava/util/Optional;"
			},
			at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;")
	)
	private <T> void push(CallbackInfoReturnable<Optional<T>> cir) {
		if (SpecterSerialization.CURRENT_TRANSLATABLE.get().contains((TranslatableTextContent) (Object) this))
			throw new IllegalStateException("Detected recursive translation: " + key);

		SpecterSerialization.CURRENT_TRANSLATABLE.get().push((TranslatableTextContent) (Object) this);
	}


	@Inject(
			method = {
					"visit(Lnet/minecraft/text/StringVisitable$StyledVisitor;Lnet/minecraft/text/Style;)Ljava/util/Optional;",
					"visit(Lnet/minecraft/text/StringVisitable$Visitor;)Ljava/util/Optional;"
			},
			at = @At("RETURN")
	)
	private <T> void pop(CallbackInfoReturnable<Optional<T>> cir) {
		SpecterSerialization.CURRENT_TRANSLATABLE.get().pop();

		if (SpecterSerialization.CURRENT_TRANSLATABLE.get().isEmpty())
			SpecterSerialization.CURRENT_TRANSLATABLE.remove();
	}


	@Inject(
			method = "updateTranslations",
			at = {
					@At(value = "INVOKE", target = "Lnet/minecraft/util/Language;get(Ljava/lang/String;)Ljava/lang/String;"),
					@At(value = "INVOKE", target = "Lnet/minecraft/util/Language;get(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;")
			},
			cancellable = true
	)
	private void updateTranslations(CallbackInfo ci, @Local Language language) {
		if (!(language instanceof TextTranslationSupplier supplier))
			return;

		Text text = supplier.specter_serialization$getText(key);
		if (text == null) return;

		translations = ImmutableList.of(text);
		ci.cancel();
	}
}
