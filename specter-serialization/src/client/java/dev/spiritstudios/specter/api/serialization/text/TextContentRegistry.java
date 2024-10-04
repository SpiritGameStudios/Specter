package dev.spiritstudios.specter.api.serialization.text;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.text.TextContent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

public final class TextContentRegistry {
	private static final Map<String, Entry<?>> types = new Object2ObjectOpenHashMap<>();

	public static void register(String field, TextContent.Type<?> type) {
		types.put(type.id(), new Entry<>(field, type));
	}

	@ApiStatus.Internal
	public static Map<String, Entry<?>> getTypes() {
		return ImmutableMap.copyOf(types);
	}

	@ApiStatus.Internal
	public record Entry<T extends TextContent>(String field, TextContent.Type<T> type) {

	}
}
