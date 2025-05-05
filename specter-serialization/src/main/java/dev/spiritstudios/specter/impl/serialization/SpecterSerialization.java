package dev.spiritstudios.specter.impl.serialization;

import java.util.ArrayDeque;
import java.util.Deque;

import com.google.common.collect.ImmutableMap;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

import net.fabricmc.api.ModInitializer;

import dev.spiritstudios.specter.api.serialization.text.DynamicTextContent;
import dev.spiritstudios.specter.api.serialization.text.TextContentRegistry;

public class SpecterSerialization implements ModInitializer {
	public static final ThreadLocal<ImmutableMap.Builder<String, Text>> TEXT_TRANSLATIONS_BUILDER = ThreadLocal.withInitial(ImmutableMap.Builder::new);

	public static final ThreadLocal<Deque<TranslatableTextContent>> CURRENT_TRANSLATABLE = ThreadLocal.withInitial(ArrayDeque::new);

	@Override
	public void onInitialize() {
		TextContentRegistry.register("index", DynamicTextContent.TYPE);
	}
}
