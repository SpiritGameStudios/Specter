package dev.spiritstudios.specter.api.serialization.toml;

import com.mojang.serialization.DynamicOps;
import dev.spiritstudios.specter.api.serialization.Commentable;
import org.jetbrains.annotations.Nullable;
import org.tomlj.TomlPosition;
import org.tomlj.TomlTable;

import java.util.List;

/**
 * A base class for all TOML elements.
 * Only exists so that we can make a {@link DynamicOps DynamicOps}. Otherwise, we would just use Tomlj's types.
 */
public abstract class TomlElement implements Commentable {
	@Nullable
	private final TomlPosition position;
	private List<String> comments;

	protected TomlElement(@Nullable TomlPosition position, String... comments) {
		this.position = position;
		this.comments = List.of(comments);
	}

	protected TomlElement(String... comments) {
		this(null, comments);
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
		return comments;
	}

	@Override
	public void setComments(List<String> comments) {
		this.comments = comments;
	}

	public @Nullable TomlPosition position() {
		return position;
	}
}
