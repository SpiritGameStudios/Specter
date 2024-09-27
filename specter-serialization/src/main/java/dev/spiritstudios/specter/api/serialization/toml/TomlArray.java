package dev.spiritstudios.specter.api.serialization.toml;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class TomlArray extends TomlElement implements Iterable<TomlElement> {
	private final List<TomlElement> elements = new ArrayList<>();

	public TomlArray() {
	}

	public static TomlArray ofTomljArray(org.tomlj.TomlArray valueArray) {
		TomlArray tomlArray = new TomlArray();
		for (Object value : valueArray.toList()) {
			switch (value) {
				case org.tomlj.TomlTable valueTable -> {
					TomlTableElement tomlValueTable = new TomlTableElement(valueTable);
					tomlArray.add(tomlValueTable);
				}
				case org.tomlj.TomlArray array -> {
					TomlArray tomlArrayValue = ofTomljArray(array);
					tomlArray.add(tomlArrayValue);
				}
				default -> tomlArray.add(TomlPrimitive.of(value));
			}
		}
		return tomlArray;
	}

	public void add(TomlElement element) {
		elements.add(element);
	}

	public void addAll(List<TomlElement> elements) {
		this.elements.addAll(elements);
	}

	public Stream<TomlElement> stream() {
		return elements.stream();
	}

	public List<TomlElement> elements() {
		return ImmutableList.copyOf(elements);
	}

	@Override
	public @NotNull Iterator<TomlElement> iterator() {
		return ImmutableList.copyOf(elements).iterator();
	}
}
