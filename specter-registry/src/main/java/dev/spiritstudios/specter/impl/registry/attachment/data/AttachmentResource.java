package dev.spiritstudios.specter.impl.registry.attachment.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import net.minecraft.util.Identifier;

import java.util.List;

public record AttachmentResource<V>(boolean replace, List<Pair<Identifier, V>> entries) {
	public static <V> Codec<AttachmentResource<V>> resourceCodecOf(Attachment<?, V> attachment) {
		return RecordCodecBuilder.create(instance -> instance.group(
				Codec.BOOL.optionalFieldOf("replace", false).forGetter(AttachmentResource::replace),
				Codec.compoundList(
						Identifier.CODEC,
						attachment.getCodec()
				).fieldOf("values").forGetter(AttachmentResource::entries)
		).apply(instance, AttachmentResource::new));
	}
}
