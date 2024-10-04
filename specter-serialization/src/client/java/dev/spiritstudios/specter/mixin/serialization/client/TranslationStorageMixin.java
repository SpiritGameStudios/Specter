package dev.spiritstudios.specter.mixin.serialization.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.spiritstudios.specter.impl.serialization.SpecterSerializationClient;
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

	@WrapOperation(method = "load(Lnet/minecraft/resource/ResourceManager;Ljava/util/List;Z)Lnet/minecraft/client/resource/language/TranslationStorage;", at = @At(value = "NEW", target = "(Ljava/util/Map;Z)Lnet/minecraft/client/resource/language/TranslationStorage;"))
	private static TranslationStorage skipImmutable(Map<String, String> translations, boolean rightToLeft, Operation<TranslationStorage> original, @Local Map<String, String> map) {
		TranslationStorage storage = original.call(map, rightToLeft);
		((TranslationStorageMixin) (Object) storage).textTranslations = SpecterSerializationClient.TEXT_TRANSLATIONS_BUILDER.get().build();

		SpecterSerializationClient.TEXT_TRANSLATIONS_BUILDER.remove();
		return storage;
	}

	@ModifyReturnValue(method = "hasTranslation", at = @At("RETURN"))
	private boolean hasTranslation(boolean original, @Local(argsOnly = true) String key) {
		if (textTranslations == null) return original;
		return original || textTranslations.containsKey(key);
	}

	@ModifyReturnValue(method = "get", at = @At("RETURN"))
	private String get(String original) {
		if (textTranslations == null) return original;

		Text text = textTranslations.get(original);
		return text != null ? text.getString() : original;
	}

	@Override
	public Text specter_serialization$getText(String key) {
		if (textTranslations == null) return null;
		return textTranslations.get(key);
	}
}
