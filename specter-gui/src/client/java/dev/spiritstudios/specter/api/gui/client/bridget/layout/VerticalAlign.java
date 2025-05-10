package dev.spiritstudios.specter.api.gui.client.bridget.layout;

import java.util.function.BiFunction;

public enum VerticalAlign {
	TOP((itemHeight, span) -> 0),
	BOTTOM((itemHeight, span) -> span - itemHeight),
	CENTER((itemHeight, span) -> span / 2 - itemHeight / 2);

	private final BiFunction<Integer, Integer, Integer> align;

	VerticalAlign(BiFunction<Integer, Integer, Integer> align) {
		this.align = align;
	}

	public int align(int itemHeight, int span) {
		return align.apply(itemHeight, span);
	}
}
