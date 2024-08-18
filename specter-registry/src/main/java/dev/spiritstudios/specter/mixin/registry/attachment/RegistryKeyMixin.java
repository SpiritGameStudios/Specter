package dev.spiritstudios.specter.mixin.registry.attachment;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import dev.spiritstudios.specter.impl.registry.attachment.AttachmentHolder;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Mixin(RegistryKey.class)
public class RegistryKeyMixin<R> implements AttachmentHolder<R> {
	@Unique
	private Map<Identifier, Attachment<R, ?>> attachments;

	@Unique
	private final Table<Attachment<R, ?>, R, Object> values = Tables.newCustomTable(new Object2ReferenceOpenHashMap<>(), Reference2ObjectOpenHashMap::new);

	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(Identifier registry, Identifier value, CallbackInfo ci) {
		attachments = new HashMap<>();
	}

	@Override
	public void specter$registerAttachment(Attachment<R, ?> attachment) {
		attachments.put(attachment.getId(), attachment);
	}

	@Override
	public @Nullable Attachment<R, ?> specter$getAttachment(Identifier id) {
		return this.attachments.get(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> V specter$getAttachmentValue(Attachment<R, V> attachment, R entry) {
		return (V) values.get(attachment, entry);
	}

	@Override
	public Set<Map.Entry<Identifier, Attachment<R, ?>>> specter$getAttachments() {
		return attachments.entrySet();
	}

	@Override
	public Table<Attachment<R, ?>, R, Object> specter$getValues() {
		return values;
	}

	@Override
	public <T> void specter$putAttachmentValue(Attachment<R, T> attachment, R entry, T value) {
		values.put(attachment, entry, value);
	}
}
