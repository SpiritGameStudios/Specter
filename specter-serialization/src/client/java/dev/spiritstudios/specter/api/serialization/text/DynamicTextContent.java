package dev.spiritstudios.specter.api.serialization.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.specter.impl.serialization.SpecterSerializationClient;
import net.minecraft.text.*;

import java.util.Optional;

/**
 * A text content that is resolved at runtime.
 *
 * @param index The index of the argument to resolve this content to.
 */
public record DynamicTextContent(int index) implements TextContent {
	public static MapCodec<DynamicTextContent> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(Codec.INT.fieldOf("index").forGetter(DynamicTextContent::index)).apply(instance, DynamicTextContent::new)
	);

	public static final TextContent.Type<DynamicTextContent> TYPE = new Type<>(
		CODEC,
		"dynamic"
	);

	@Override
	public Type<?> getType() {
		return TYPE;
	}

	@Override
	public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
		TranslatableTextContent parent = SpecterSerializationClient.CURRENT_TRANSLATABLE.get().peek();
		if (parent == null || parent.getArgs().length <= index)
			return visitor.accept("{" + index + "}");

		Object arg = parent.getArgs()[index];
		if (arg instanceof Text text) return text.visit(visitor);
		return visitor.accept(arg.toString());
	}

	@Override
	public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
		TranslatableTextContent parent = SpecterSerializationClient.CURRENT_TRANSLATABLE.get().peek();
		if (parent == null || parent.getArgs().length <= index)
			return visitor.accept(style, "{" + index + "}");

		Object arg = parent.getArgs()[index];
		if (arg instanceof Text text) return text.visit(visitor, style);
		return visitor.accept(style, arg.toString());
	}
}
