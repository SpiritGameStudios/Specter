package dev.spiritstudios.specter.mixin.serialization.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.spiritstudios.specter.impl.serialization.SpecterSerialization;
import dev.spiritstudios.specter.impl.serialization.text.TextTranslationSupplier;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(TranslationStorage.class)
public class TranslationStorageMixin implements TextTranslationSupplier {
	@Unique
	private Map<String, Text> textTranslations;

	@ModifyReturnValue(method = "load(Lnet/minecraft/resource/ResourceManager;Ljava/util/List;Z)Lnet/minecraft/client/resource/language/TranslationStorage;", at = @At("RETURN"))
	private static TranslationStorage load(TranslationStorage original) {
		((TranslationStorageMixin) (Object) original).textTranslations = SpecterSerialization.TEXT_TRANSLATIONS_BUILDER.get().build();

		SpecterSerialization.TEXT_TRANSLATIONS_BUILDER.remove();
		return original;
	}

	@ModifyReturnValue(method = "hasTranslation", at = @At("RETURN"))
	private boolean hasTranslation(boolean original, @Local(argsOnly = true) String key) {
		if (textTranslations == null) return original;
		return original || textTranslations.containsKey(key);
	}

	@ModifyReturnValue(method = "get", at = @At("RETURN"))
	private String get(String original) {
		Text text = textTranslations.get(original);
		return text != null ? text.getString() : original;
	}

	@Override
	public Text specter_serialization$getText(String key) {
		return textTranslations.get(key);
	}
}
