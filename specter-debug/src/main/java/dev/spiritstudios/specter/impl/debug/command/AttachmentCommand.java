package dev.spiritstudios.specter.impl.debug.command;

import com.google.common.collect.Streams;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import dev.spiritstudios.specter.impl.registry.attachment.AttachmentHolder;
import dev.spiritstudios.specter.impl.registry.attachment.data.AttachmentResource;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Function;

public final class AttachmentCommand {
	public static final SuggestionProvider<ServerCommandSource> REGISTRY_SUGGESTIONS = (context, builder) ->
		CommandSource.suggestIdentifiers(Registries.ROOT.stream()
			.filter(registry -> !AttachmentHolder.of(registry).specter$getAttachments().isEmpty())
			.map(registry -> registry.getKey().getValue()), builder);

	public static final Function<String, SuggestionProvider<ServerCommandSource>> ATTACHMENT_SUGGESTIONS = (registryArg) -> (context, builder) -> {
		Registry<?> registry = getRegistryFromContext(context, registryArg);
		if (registry == null) return null;
		return CommandSource.suggestIdentifiers(
			AttachmentHolder.of(registry)
				.specter$getAttachments()
				.stream()
				.map(Map.Entry::getKey),
			builder
		);
	};


	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("attachment")
			.then(CommandManager.literal("dump")
				.then(CommandManager.argument("registry", IdentifierArgumentType.identifier())
					.suggests(REGISTRY_SUGGESTIONS)
					.then(CommandManager.argument("attachment", IdentifierArgumentType.identifier())
						.suggests(ATTACHMENT_SUGGESTIONS.apply("registry"))
						.executes(context -> dump(
							context,
							getAttachmentFromContext(context)
						))
					)
				)
			));
	}

	private static Registry<?> getRegistryFromContext(CommandContext<ServerCommandSource> context, String registryArg) {
		return Registries.ROOT.get(context.getArgument(registryArg, Identifier.class));
	}

	private static Attachment<?, ?> getAttachmentFromContext(CommandContext<ServerCommandSource> context) {
		Registry<?> registry = getRegistryFromContext(context, "registry");
		Identifier attachmentId = context.getArgument("attachment", Identifier.class);
		return AttachmentHolder.of(registry).specter$getAttachment(attachmentId);
	}

	private static <R, V> int dump(CommandContext<ServerCommandSource> context, Attachment<R, V> attachment) {
		Codec<AttachmentResource<V>> codec = AttachmentResource.resourceCodecOf(attachment);
		AttachmentResource<V> resource = new AttachmentResource<>(
			false,
			Streams.stream(attachment.iterator())
				.map(entry -> Pair.of(attachment.getRegistry().getId(entry.key()), entry.value()))
				.toList()
		);

		context.getSource().sendFeedback(() ->
			Text.of(codec.encodeStart(JsonOps.INSTANCE, resource).getOrThrow().toString()), true);
		return Command.SINGLE_SUCCESS;
	}
}
