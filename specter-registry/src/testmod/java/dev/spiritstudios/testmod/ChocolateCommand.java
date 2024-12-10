package dev.spiritstudios.testmod;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.spiritstudios.specter.api.registry.reloadable.SpecterReloadableRegistries;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

public final class ChocolateCommand {
	public static final SuggestionProvider<ServerCommandSource> CHOCOLATE_SUGGESTIONS = (context, builder) -> {
		Optional<DynamicRegistryManager.Immutable> reloadableManager = SpecterReloadableRegistries.reloadableManager();
		if (reloadableManager.isEmpty()) return builder.buildFuture();

		return CommandSource.suggestIdentifiers(
			reloadableManager.get().get(SpecterRegistryTestMod.CHOCOLATE_KEY).getKeys().stream()
				.map(RegistryKey::getValue),
			builder
		);

	};

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access) {
		dispatcher.register(CommandManager.literal("chocolate")
			.then(CommandManager.argument(
				"value",
				IdentifierArgumentType.identifier()
			).suggests(CHOCOLATE_SUGGESTIONS).executes(context -> {
				Identifier chocolateId = IdentifierArgumentType.getIdentifier(context, "value");
				Optional<Chocolate> chocolateEntry = SpecterReloadableRegistries.reloadableManager().flatMap(manager ->
					manager.get(SpecterRegistryTestMod.CHOCOLATE_KEY).getOrEmpty(chocolateId));

				if (chocolateEntry.isEmpty()) {
					context.getSource().sendError(Text.literal("Invalid id"));
					return 0;
				}

				Chocolate chocolate = chocolateEntry.get();
				NbtElement output = Chocolate.CODEC.encodeStart(NbtOps.INSTANCE, chocolate).getOrThrow();
				context.getSource().sendFeedback(() -> NbtHelper.toPrettyPrintedText(output), false);

				return Command.SINGLE_SUCCESS;
			})));
	}
}
