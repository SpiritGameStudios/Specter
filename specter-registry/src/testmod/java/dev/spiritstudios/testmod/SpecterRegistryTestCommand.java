package dev.spiritstudios.testmod;

import com.google.common.collect.Streams;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import dev.spiritstudios.specter.impl.registry.attachment.AttachmentHolder;
import dev.spiritstudios.specter.impl.registry.attachment.data.AttachmentResource;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class SpecterRegistryTestCommand {
	public static class RegistryArgumentType implements ArgumentType<Identifier> {

		protected final CommandRegistryAccess registryAccess;
		public RegistryArgumentType(CommandRegistryAccess access) {
			this.registryAccess = access;
		}

		@Override
		public Identifier parse(StringReader reader) throws CommandSyntaxException {
			return Identifier.fromCommandInput(reader);
		}

		@Override
		public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
			return context.getSource() instanceof CommandSource
				? CommandSource.suggestIdentifiers(this.registryAccess.streamAllRegistryKeys().map(RegistryKey::getValue), builder)
				: Suggestions.empty();
		}
	}

	public static class AttachmentArgumentType implements ArgumentType<Identifier> {

		protected final CommandRegistryAccess registryAccess;
		public AttachmentArgumentType(CommandRegistryAccess access) {
			this.registryAccess = access;
		}

		@Override
		public Identifier parse(StringReader reader) throws CommandSyntaxException {
			return Identifier.fromCommandInput(reader);
		}

		@Override
		public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
			var regID = context.getArgument("registry-id", Identifier.class);
			return context.getSource() instanceof CommandSource
				? CommandSource.suggestIdentifiers(this.registryAccess.getWrapperOrThrow(RegistryKey.ofRegistry(regID)).streamKeys().map(RegistryKey::getValue), builder)
				: Suggestions.empty();
		}
	}

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(
			CommandManager.literal("specter-test-registry")
				.then(
					CommandManager.literal("dump")
						.executes(ctx -> {
							ctx.getSource().sendFeedback(() -> Text.literal("result printed to terminal"), true);
							var holder = AttachmentHolder.of(Registries.BLOCK);
							var attachment = (Attachment< Block, Integer>)holder.specter$getAttachment(SpecterRegistryTestMod.ATTACHMENT_ID);
							attachment.put(Blocks.STONE, 420);
							System.out.println(dump(attachment));
							return Command.SINGLE_SUCCESS;
					})
				)
//
		);
//		dispatcher.register(
//				CommandManager.literal("specter-test-registry")
//						.then(CommandManager.argument("registry-id", new RegistryArgumentType(registryAccess))
//								.then(
//										CommandManager.argument("attachment-id", new AttachmentArgumentType(registryAccess))
//								).executes(ctx -> {
//									System.out.println("wawa");
//									var holder = AttachmentHolder.of(Registries.ROOT.get(ctx.getArgument("registry-id", Identifier.class)));
//									var attachment = holder.specter$getAttachment(ctx.getArgument("attachment-id", Identifier.class));
//									System.out.println(dump(attachment));
//									ctx.getSource().sendFeedback(() -> Text.literal("result printed to terminal"), true);
//									return Command.SINGLE_SUCCESS;
//								}))
//		);
	}

	private static <R, V> String dump(Attachment<R, V> attachment) {
		var codec = AttachmentResource.resourceCodecOf(attachment);
		var resource = new AttachmentResource<V>(false,
			Streams.stream(attachment.iterator())
				.map(e -> Pair.of(attachment.getRegistry().getId(e.entry()), e.value()))
				.toList()
		);
		return codec.encodeStart(JsonOps.INSTANCE, resource).toString();
	}
}
