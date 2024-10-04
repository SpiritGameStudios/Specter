package dev.spiritstudios.specter.mixin.serialization.client;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.spiritstudios.specter.impl.serialization.StyledTranslatableVisitor;
import dev.spiritstudios.specter.impl.serialization.TextTranslationSupplier;
import dev.spiritstudios.specter.impl.serialization.TranslatableVisitor;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(TranslatableTextContent.class)
public abstract class TranslatableTextContentMixin {
	@Shadow
	@Final
	private String key;

	@Shadow
	private List<StringVisitable> translations;

	@Shadow
	public abstract Object[] getArgs();

	@Inject(method = "updateTranslations", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Language;get(Ljava/lang/String;)Ljava/lang/String;"), cancellable = true)
	private void updateTranslations(CallbackInfo ci) {
		Language language = Language.getInstance();
		if (!(language instanceof TextTranslationSupplier supplier))
			return;

		Text text = supplier.specter_serialization$getText(key);
		if (text == null) return;

		translations = ImmutableList.of(text);
		ci.cancel();
	}

	@Inject(method = "visit(Lnet/minecraft/text/StringVisitable$Visitor;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/TranslatableTextContent;updateTranslations()V", shift = At.Shift.AFTER))
	private <T> void visit(StringVisitable.Visitor<T> visitor, CallbackInfoReturnable<Optional<T>> cir, @Share("translatableVisitor") LocalRef<TranslatableVisitor<T>> translatableVisitorRef) {
		translatableVisitorRef.set(new TranslatableVisitor<>(visitor, getArgs()));
	}

	@Inject(method = "visit(Lnet/minecraft/text/StringVisitable$StyledVisitor;Lnet/minecraft/text/Style;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/TranslatableTextContent;updateTranslations()V", shift = At.Shift.AFTER))
	private <T> void visit(StringVisitable.StyledVisitor<T> visitor, Style style, CallbackInfoReturnable<Optional<T>> cir, @Share("translatableVisitor") LocalRef<StyledTranslatableVisitor<T>> translatableVisitorRef) {
		translatableVisitorRef.set(new StyledTranslatableVisitor<>(visitor, getArgs()));
	}

	@WrapOperation(method = "visit(Lnet/minecraft/text/StringVisitable$Visitor;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/StringVisitable;visit(Lnet/minecraft/text/StringVisitable$Visitor;)Ljava/util/Optional;"))
	private <T> Optional<T> visitWrapped(StringVisitable instance, StringVisitable.Visitor<T> tVisitor, Operation<Optional<T>> original, @Share("translatableVisitor") LocalRef<TranslatableVisitor<T>> translatableVisitorRef) {
		return original.call(instance, translatableVisitorRef.get());
	}

	@WrapOperation(method = "visit(Lnet/minecraft/text/StringVisitable$StyledVisitor;Lnet/minecraft/text/Style;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/StringVisitable;visit(Lnet/minecraft/text/StringVisitable$StyledVisitor;Lnet/minecraft/text/Style;)Ljava/util/Optional;"))
	private <T> Optional<T> visitWrapped(StringVisitable instance, StringVisitable.StyledVisitor<T> tStyledVisitor, Style style, Operation<Optional<T>> original, @Share("translatableVisitor") LocalRef<StyledTranslatableVisitor<T>> translatableVisitorRef) {
		return original.call(instance, translatableVisitorRef.get(), style);
	}
}
