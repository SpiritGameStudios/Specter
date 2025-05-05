package dev.spiritstudios.testmod.registry;

import java.util.Optional;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.registry.reloadable.SpecterReloadableRegistries;

public final class ChocolateCommand {
	public static final SuggestionProvider<ServerCommandSource> CHOCOLATE_SUGGESTIONS = (context, builder) -> {
		Optional<RegistryWrapper.WrapperLookup> lookup = SpecterReloadableRegistries.lookup();
		if (lookup.isEmpty()) return builder.buildFuture();

		return CommandSource.suggestIdentifiers(
			lookup.get().getOrThrow(SpecterRegistryTestMod.CHOCOLATE_KEY).streamKeys()
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
				Optional<Chocolate> chocolateEntry = SpecterReloadableRegistries.lookup().flatMap(lookup -> lookup.getOrThrow(SpecterRegistryTestMod.CHOCOLATE_KEY)
					.getOptional(RegistryKey.of(
						SpecterRegistryTestMod.CHOCOLATE_KEY,
						chocolateId
					)).map(RegistryEntry.Reference::value));

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
