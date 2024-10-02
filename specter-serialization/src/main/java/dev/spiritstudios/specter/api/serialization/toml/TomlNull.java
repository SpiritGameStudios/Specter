package dev.spiritstudios.specter.api.serialization.toml;

public class TomlNull extends TomlElement {
	public static final TomlNull INSTANCE = new TomlNull();

	private TomlNull() {
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof TomlNull;
	}

	@Override
	public int hashCode() {
		return 31;
	}
}
