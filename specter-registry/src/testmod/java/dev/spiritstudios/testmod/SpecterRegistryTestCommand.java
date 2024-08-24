package dev.spiritstudios.testmod;

import com.google.common.collect.Streams;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import dev.spiritstudios.specter.impl.registry.attachment.AttachmentHolder;
import dev.spiritstudios.specter.impl.registry.attachment.data.AttachmentResource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class SpecterRegistryTestCommand {
	public static class RegistryArgumentType implements ArgumentType<Registry<?>> {
		@Override
		public Registry<?> parse(StringReader reader) throws CommandSyntaxException {
			return Registries.ROOT.get(Identifier.fromCommandInput(reader));
		}

		@Override
		public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
			return context.getSource() instanceof CommandSource
				? CommandSource.suggestIdentifiers(Registries.ROOT.stream().map(r -> r.getKey().getValue()), builder)
				: Suggestions.empty();
		}
	}

	public static class AttachmentArgumentType implements ArgumentType<Identifier> {

		protected final String registryArg;
		public AttachmentArgumentType(String registryArg) {
			this.registryArg = registryArg;
		}

		@Override
		public Identifier parse(StringReader reader) throws CommandSyntaxException {
			return Identifier.fromCommandInput(reader);
		}

		@Override
		public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
			Registry<?> registry = context.getArgument(registryArg, Registry.class);
			CommandSource.suggestIdentifiers(registry.getKeys().stream().map(RegistryKey::getValue), builder);
			return CommandSource.suggestIdentifiers(registry.getKeys().stream().map(RegistryKey::getValue), builder);
		}

	}

	private static Attachment<?, ?> getAttachmentFromContext(CommandContext<ServerCommandSource> ctx) {
		Registry<?> registry = ctx.getArgument("registry", Registry.class);
		Identifier attachmentId = ctx.getArgument("attachment", Identifier.class);
		return AttachmentHolder.of(registry).specter$getAttachment(attachmentId);
	}

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(CommandManager.literal("specter-test-registry")
			.then(CommandManager.literal("dump")
				.then(CommandManager.argument("registry", new RegistryArgumentType())
					.then(CommandManager.argument("attachment", new AttachmentArgumentType("registry"))
						.executes(ctx -> {
							ctx.getSource().sendFeedback(() -> Text.literal("result printed to terminal: registry"), true);
							System.out.println(dump(getAttachmentFromContext(ctx)));
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
