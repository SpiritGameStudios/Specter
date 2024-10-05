package dev.spiritstudios.specter.impl.serialization;

import com.google.common.collect.ImmutableMap;
import dev.spiritstudios.specter.api.serialization.text.DynamicTextContent;
import dev.spiritstudios.specter.api.serialization.text.TextContentRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

import java.util.ArrayDeque;
import java.util.Deque;

public class SpecterSerialization implements ModInitializer {
	public static final ThreadLocal<ImmutableMap.Builder<String, Text>> TEXT_TRANSLATIONS_BUILDER = ThreadLocal.withInitial(ImmutableMap.Builder::new);

	public static final ThreadLocal<Deque<TranslatableTextContent>> CURRENT_TRANSLATABLE = ThreadLocal.withInitial(ArrayDeque::new);

	@Override
	public void onInitialize() {
		TextContentRegistry.register("index", DynamicTextContent.TYPE);
	}
}
