package dev.spiritstudios.specter.api.serialization.toml;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

public class TomlPrimitive extends TomlElement {
	private final Object value;

	public TomlPrimitive(String value, List<String> comments) {
		super(comments);
		this.value = value;
	}

	public TomlPrimitive(long value, List<String> comments) {
		super(comments);
		this.value = value;
	}

	public TomlPrimitive(double value, List<String> comments) {
		super(comments);
		this.value = value;
	}

	public TomlPrimitive(boolean value, List<String> comments) {
		super(comments);
		this.value = value;
	}

	public TomlPrimitive(OffsetDateTime value, List<String> comments) {
		super(comments);
		this.value = value;
	}

	public TomlPrimitive(LocalDateTime value, List<String> comments) {
		super(comments);
		this.value = value;
	}

	public TomlPrimitive(LocalDate value, List<String> comments) {
		super(comments);
		this.value = value;
	}

	public TomlPrimitive(LocalTime value, List<String> comments) {
		super(comments);
		this.value = value;
	}

	public static TomlPrimitive of(Object value, List<String> comments) {
		return switch (value) {
			case String s -> new TomlPrimitive(s, comments);
			case Integer i -> new TomlPrimitive(i.longValue(), comments);
			case Long l -> new TomlPrimitive(l, comments);
			case Float f -> new TomlPrimitive(f.doubleValue(), comments);
			case Double d -> new TomlPrimitive(d, comments);
			case Boolean b -> new TomlPrimitive(b, comments);
			case OffsetDateTime odt -> new TomlPrimitive(odt, comments);
			case LocalDateTime ldt -> new TomlPrimitive(ldt, comments);
			case LocalDate ld -> new TomlPrimitive(ld, comments);
			case LocalTime lt -> new TomlPrimitive(lt, comments);
			default -> throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
		};
	}

	public static TomlPrimitive of(Object value) {
		return of(value, Collections.emptyList());
	}

	public Object value() {
		return value;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		TomlPrimitive that = (TomlPrimitive) other;
		if (value == null) return that.value == null;
		return value.equals(that.value);
	}

	@Override
	public String toString() {
		if (value instanceof String s) return s;
		return value.toString();
	}

	@Override
	public int hashCode() {
		return value != null ? value.hashCode() : 31;
	}
}
