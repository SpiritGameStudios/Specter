package dev.spiritstudios.specter.impl.registry.attachment;

import com.google.common.collect.Table;
import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public interface AttachmentHolder<R> {
	@SuppressWarnings("unchecked")
	static <R> AttachmentHolder<R> of(Registry<R> registry) {
		return (AttachmentHolder<R>) registry.getKey();
	}

	void specter$registerAttachment(Attachment<R, ?> attachment);

	@Nullable
	Attachment<R, ?> specter$getAttachment(Identifier id);

	<V> V specter$getAttachmentValue(Attachment<R, V> attachment, R entry);

	Set<Map.Entry<Identifier, Attachment<R, ?>>> specter$getAttachments();

	Table<Attachment<R, ?>, R, Object> specter$getValues();

	<T> void specter$putAttachmentValue(Attachment<R, T> attachment, R entry, T value);
}
