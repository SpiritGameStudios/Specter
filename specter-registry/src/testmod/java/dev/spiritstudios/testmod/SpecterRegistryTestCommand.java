package dev.spiritstudios.testmod;

import com.google.common.collect.Streams;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import dev.spiritstudios.specter.impl.registry.attachment.AttachmentHolder;
import dev.spiritstudios.specter.impl.registry.attachment.data.AttachmentResource;
import net.minecraft.command.CommandRegistryAccess;
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

public class SpecterRegistryTestCommand {

	private static final SuggestionProvider<ServerCommandSource> REGISTRY_SUGGESTIONS = (context, builder) ->
			CommandSource.suggestIdentifiers(Registries.ROOT.stream()
				.filter(registry -> AttachmentHolder.of(registry).specter$getAttachments().size() > 0)
				.map(registry -> registry.getKey().getValue()), builder);


	private static final Function<String, SuggestionProvider<ServerCommandSource>> ATTACHMENT_SUGGESTIONS = (registryArg) -> (context, builder) -> {
		Registry<?> registry = getRegistryFromContext(context, registryArg);
		return CommandSource.suggestIdentifiers(AttachmentHolder.of(registry).specter$getAttachments().stream().map(Map.Entry::getKey), builder);
	};

	private static Registry<?> getRegistryFromContext(CommandContext<ServerCommandSource> context, String registryArg) {
		return Registries.ROOT.get(context.getArgument(registryArg, Identifier.class));
	}

	private static Attachment<?, ?> getAttachmentFromContext(CommandContext<ServerCommandSource> context, String registryArg, String attachmentArg) {
		Registry<?> registry = getRegistryFromContext(context, registryArg);
		Identifier attachmentId = context.getArgument(attachmentArg, Identifier.class);
		return AttachmentHolder.of(registry).specter$getAttachment(attachmentId);
	}

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("specter-test-registry")
			.then(CommandManager.literal("dump")
				.then(CommandManager.argument("registry", IdentifierArgumentType.identifier())
					.suggests(REGISTRY_SUGGESTIONS)
					.then(CommandManager.argument("attachment", IdentifierArgumentType.identifier())
						.suggests(ATTACHMENT_SUGGESTIONS.apply("registry"))
						.executes(context -> {
							context.getSource().sendFeedback(() -> Text.literal("attachment data printed to terminal"), true);
							SpecterGlobals.LOGGER.info(dump(getAttachmentFromContext(context, "registry", "attachment")));
							return Command.SINGLE_SUCCESS;
						})
					)
				)
			)
		);
	}

	private static <R, V> String dump(Attachment<R, V> attachment) {
		var codec = AttachmentResource.resourceCodecOf(attachment);
		var resource = new AttachmentResource<V>(false,
			Streams.stream(attachment.iterator())
				.map(e -> Pair.of(attachment.getRegistry().getId(e.entry()), e.value()))
				.toList()
		);
		return codec.encodeStart(JsonOps.INSTANCE, resource).getOrThrow().toString();
	}
}
