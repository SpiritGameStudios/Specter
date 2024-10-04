package dev.spiritstudios.specter.impl.serialization;

import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;

import java.util.Optional;

public class StyledTranslatableVisitor<T> implements StringVisitable.StyledVisitor<T> {
	private final StringVisitable.StyledVisitor<T> visitor;
	private final Object[] args;

	public StyledTranslatableVisitor(StringVisitable.StyledVisitor<T> visitor, Object[] args) {
		this.visitor = visitor;
		this.args = args;
	}


	public Object[] getArgs() {
		return args;
	}

	@Override
	public Optional<T> accept(Style style, String asString) {
		return visitor.accept(style, asString);
	}
}
