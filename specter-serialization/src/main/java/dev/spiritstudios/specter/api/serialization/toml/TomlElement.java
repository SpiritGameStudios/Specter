package dev.spiritstudios.specter.api.serialization.toml;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.DynamicOps;
import dev.spiritstudios.specter.api.serialization.Commentable;
import org.tomlj.TomlTable;

import java.util.Collections;
import java.util.List;

/**
 * A base class for all TOML elements.
 * Only exists so that we can make a {@link DynamicOps DynamicOps}. Otherwise, we would just use Tomlj's types.
 */
public abstract class TomlElement implements Commentable {
	private List<String> comments;

	protected TomlElement(List<String> comments) {
		this.comments = comments;
	}

	protected TomlElement() {
		this.comments = Collections.emptyList();
	}

	public static TomlElement of(Object value) {
		return switch (value) {
			case null -> TomlNull.INSTANCE;
			case TomlTable table -> new TomlTableElement(table);
			case org.tomlj.TomlArray array -> TomlArray.ofTomljArray(array);
			default -> TomlPrimitive.of(value);
		};
	}

	public Object to() {
		return switch (this) {
			case TomlNull nullValue -> null;
			case TomlTableElement table -> table;
			case TomlArray array -> array;
			case TomlPrimitive primitive -> primitive.value();
			default -> throw new IllegalStateException("Unexpected value: " + this);
		};
	}

	@Override
	public List<String> comments() {
		return ImmutableList.copyOf(comments);
	}

	@Override
	public void setComments(List<String> comments) {
		this.comments = comments;
	}
}
