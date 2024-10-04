package dev.spiritstudios.specter.impl.serialization.text;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

// This is so hacky and I love it
public class TranslationEntryMap<K, V> extends HashMap<K, V> {
	private final Map<String, Text> translations = new Object2ObjectOpenHashMap<>();

	public void put(String s, Text text) {
		translations.put(s, text);
	}

	public Map<String, Text> translations() {
		return ImmutableMap.copyOf(translations);
	}
}
