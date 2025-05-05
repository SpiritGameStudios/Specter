package dev.spiritstudios.specter.impl.debug.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import dev.spiritstudios.specter.api.registry.reloadable.SpecterReloadableRegistries;
import dev.spiritstudios.specter.impl.registry.reloadable.SpecterReloadableRegistriesImpl;

public class DumpReloadableRegistryCommand {
	public static final SuggestionProvider<ServerCommandSource> REGISTRY_SUGGESTIONS = (context, builder) ->
		CommandSource.suggestIdentifiers(SpecterReloadableRegistries.lookup().orElseThrow()
			.stream()
			.map(registry -> registry.getKey().getValue()), builder);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access) {
		dispatcher.register(CommandManager.literal("dumpRDR")
			.then(CommandManager.argument("registry", IdentifierArgumentType.identifier())
				.suggests(REGISTRY_SUGGESTIONS)
				.executes(context ->
					execute(SpecterReloadableRegistriesImpl.reloadableRegistries()
						.values().stream()
						.filter(info ->
							info.key().equals(RegistryKey.ofRegistry(IdentifierArgumentType.getIdentifier(context, "registry"))))
						.findAny()
						.orElseThrow(), context))));
	}

	private static <T> int execute(SpecterReloadableRegistriesImpl.ReloadableRegistryInfo<T> info, CommandContext<ServerCommandSource> context) {
		RegistryWrapper.Impl<T> lookup = SpecterReloadableRegistries.lookup().orElseThrow()
			.getOrThrow(info.key());

		lookup.streamEntries().forEach(ref -> {
			NbtElement output = info.codec().encodeStart(NbtOps.INSTANCE, ref.value()).getOrThrow();

			context.getSource().sendFeedback(() -> NbtHelper.toPrettyPrintedText(output), false);
		});

		return Command.SINGLE_SUCCESS;
	}
}
