package dev.spiritstudios.specter.impl.serialization.text;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.text.TextContent;

public final class TextContentRegistryImpl {
	private static final Map<String, Entry<?>> types = new Object2ObjectOpenHashMap<>();

	public static Map<String, Entry<?>> getTypes() {
		return ImmutableMap.copyOf(types);
	}

	public static void register(String field, TextContent.Type<?> type) {
		types.put(type.id(), new Entry<>(field, type));
	}

	public record Entry<T extends TextContent>(String field, TextContent.Type<T> type) {
	}
}
