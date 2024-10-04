package dev.spiritstudios.specter.api.serialization.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.specter.impl.serialization.StyledTranslatableVisitor;
import dev.spiritstudios.specter.impl.serialization.TranslatableVisitor;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;

import java.util.Optional;

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
		if (!(visitor instanceof TranslatableVisitor<T> translatableVisitor) || translatableVisitor.getArgs().length <= index)
			return visitor.accept("{" + index + "}");

		Object object = translatableVisitor.getArgs()[index];
		if (object instanceof Text text) return text.visit(visitor);
		else return visitor.accept(object.toString());
	}

	@Override
	public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
		if (!(visitor instanceof StyledTranslatableVisitor<T> translatableVisitor) || translatableVisitor.getArgs().length <= index)
			return visitor.accept(style, "{" + index + "}");

		Object object = translatableVisitor.getArgs()[index];
		if (object instanceof Text text) return text.visit(visitor, style);
		else return visitor.accept(style, object.toString());
	}
}
