package dev.spiritstudios.specter.impl.serialization;

import net.minecraft.text.StringVisitable;

import java.util.Optional;

public class TranslatableVisitor<T> implements StringVisitable.Visitor<T> {
	private final StringVisitable.Visitor<T> visitor;
	private final Object[] args;

	public TranslatableVisitor(StringVisitable.Visitor<T> visitor, Object[] args) {
		this.visitor = visitor;
		this.args = args;
	}


	public Object[] getArgs() {
		return args;
	}

	@Override
	public Optional<T> accept(String asString) {
		return visitor.accept(asString);
	}
}
