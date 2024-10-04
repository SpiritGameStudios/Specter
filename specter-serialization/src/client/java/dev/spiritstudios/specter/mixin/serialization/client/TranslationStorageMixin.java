package dev.spiritstudios.specter.mixin.serialization.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.spiritstudios.specter.impl.serialization.text.TextTranslationSupplier;
import dev.spiritstudios.specter.impl.serialization.text.smuggler.TranslationEntryConsumer;
import dev.spiritstudios.specter.impl.serialization.text.smuggler.TranslationEntryMap;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Mixin(TranslationStorage.class)
public class TranslationStorageMixin implements TextTranslationSupplier {
    @Unique
    public Map<String, Text> textTranslations;

    @Redirect(method = "load(Lnet/minecraft/resource/ResourceManager;Ljava/util/List;Z)Lnet/minecraft/client/resource/language/TranslationStorage;", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Maps;newHashMap()Ljava/util/HashMap;", remap = false))
    private static <K, V> HashMap<K, V> load() {
        return new TranslationEntryMap<>();
    }

    @WrapOperation(method = "load(Lnet/minecraft/resource/ResourceManager;Ljava/util/List;Z)Lnet/minecraft/client/resource/language/TranslationStorage;", at = @At(value = "NEW", target = "(Ljava/util/Map;Z)Lnet/minecraft/client/resource/language/TranslationStorage;"))
    private static TranslationStorage skipImmutable(Map<String, String> translations, boolean rightToLeft, Operation<TranslationStorage> original, @Local Map<String, String> map) {
        if (!(map instanceof TranslationEntryMap<String, String> translationEntryMap))
            return original.call(translations, rightToLeft);

        TranslationStorage storage = original.call(translationEntryMap, rightToLeft);
        ((TranslationStorageMixin) (Object) storage).textTranslations = translationEntryMap.translations();
        return storage;
    }

    @WrapOperation(method = "load(Ljava/lang/String;Ljava/util/List;Ljava/util/Map;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Language;load(Ljava/io/InputStream;Ljava/util/function/BiConsumer;)V"))
    private static void load(InputStream inputStream, BiConsumer<String, String> entryConsumer, Operation<Void> original, @Local(argsOnly = true) Map<String, String> translations) {
        if (!(translations instanceof TranslationEntryMap<String, String> translationEntryMap)) {
            original.call(inputStream, entryConsumer);
            return;
        }

        original.call(inputStream, new TranslationEntryConsumer(entryConsumer, translationEntryMap::put));
    }

    @Inject(method = "hasTranslation", at = @At(value = "HEAD"), cancellable = true)
    private void hasTranslation(String key, CallbackInfoReturnable<Boolean> cir) {
        if (textTranslations == null) return;
        if (textTranslations.containsKey(key)) cir.setReturnValue(true);
    }

    @Inject(method = "get", at = @At(value = "HEAD"), cancellable = true)
    private void get(String key, String fallback, CallbackInfoReturnable<String> cir) {
        if (textTranslations == null) return;
        Text text = textTranslations.get(key);
        if (text != null) cir.setReturnValue(text.getString());
    }

    @Override
    public Text specter_serialization$getText(String key) {
        if (textTranslations == null) return null;
        return textTranslations.get(key);
    }
}
