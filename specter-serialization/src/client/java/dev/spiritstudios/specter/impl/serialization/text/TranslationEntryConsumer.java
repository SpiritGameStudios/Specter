package dev.spiritstudios.specter.impl.serialization.text;

import net.minecraft.text.Text;

import java.util.function.BiConsumer;

public class TranslationEntryConsumer implements BiConsumer<String, String> {
	private final BiConsumer<String, String> stringConsumer;
	private final BiConsumer<String, Text> textConsumer;

	public TranslationEntryConsumer(BiConsumer<String, String> stringConsumer, BiConsumer<String, Text> textConsumer) {
		this.stringConsumer = stringConsumer;
		this.textConsumer = textConsumer;
	}

	@Override
	public void accept(String s, String s2) {
		stringConsumer.accept(s, s2);
	}

	public void accept(String s, Text text) {
		textConsumer.accept(s, text);
	}
}
