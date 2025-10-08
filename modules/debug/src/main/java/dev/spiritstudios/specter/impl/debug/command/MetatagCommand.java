package dev.spiritstudios.specter.impl.debug.command;

import java.util.Map;
import java.util.function.Function;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;

import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.api.registry.metatag.data.MetatagResource;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagHolder;

public final class MetatagCommand {
	public static final SuggestionProvider<ServerCommandSource> REGISTRY_SUGGESTIONS = (context, builder) ->
			CommandSource.suggestIdentifiers(context.getSource().getRegistryManager().streamAllRegistryKeys()
					.filter(key -> !MetatagHolder.ofAny(key).specter$getMetatags().isEmpty())
					.map(RegistryKey::getValue), builder);

	public static final Function<String, SuggestionProvider<ServerCommandSource>> METATAG_SUGGESTIONS = (registryArg) -> (context, builder) -> {
		Registry<?> registry = getRegistryFromContext(context, registryArg);
		if (registry == null) return null;
		return CommandSource.suggestIdentifiers(
				MetatagHolder.of(registry.getKey())
						.specter$getMetatags()
						.stream()
						.map(Map.Entry::getKey),
				builder
		);
	};


	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("metatag")
				.requires(source -> source.hasPermissionLevel(2))
				.then(CommandManager.literal("dump")
						.then(CommandManager.argument("registry", IdentifierArgumentType.identifier())
								.suggests(REGISTRY_SUGGESTIONS)
								.then(CommandManager.argument("metatag", IdentifierArgumentType.identifier())
										.suggests(METATAG_SUGGESTIONS.apply("registry"))
										.executes(context -> dump(
												context,
												getMetatagFromContext(context)
										))
								)
						)
				));
	}

	private static Registry<?> getRegistryFromContext(CommandContext<ServerCommandSource> context, String registryArg) {
		return context.getSource().getRegistryManager()
				.getOrThrow(RegistryKey.ofRegistry(context.getArgument(registryArg, Identifier.class)));
	}

	private static Metatag<?, ?> getMetatagFromContext(CommandContext<ServerCommandSource> context) {
		Registry<?> registry = getRegistryFromContext(context, "registry");
		Identifier metatagId = context.getArgument("metatag", Identifier.class);
		return MetatagHolder.of(registry.getKey()).specter$getMetatag(metatagId);
	}

	private static <R, V> int dump(CommandContext<ServerCommandSource> context, Metatag<R, V> metatag) {
		Codec<MetatagResource<R, V>> codec = MetatagResource.resourceCodecOf(metatag);

		DynamicRegistryManager registryManager = context.getSource().getRegistryManager();
		Registry<R> registry = registryManager.getOrThrow(metatag.registryKey());

		MetatagResource<R, V> resource = new MetatagResource<>(
				false,
				metatag.stream()
						.map(entry ->
								Pair.of(registry.getKey(entry.getKey()).orElseThrow(), entry.getValue()))
						.toList()
		);

		context.getSource().sendFeedback(() ->
				NbtHelper.toPrettyPrintedText(codec.encodeStart(NbtOps.INSTANCE, resource).getOrThrow()), true);
		return Command.SINGLE_SUCCESS;
	}
}
