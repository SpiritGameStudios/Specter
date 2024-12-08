package dev.spiritstudios.specter.impl.debug.command;

import com.google.common.collect.Streams;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.api.registry.metatag.data.MetatagResource;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagHolder;
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

public final class MetatagCommand {
	public static final SuggestionProvider<ServerCommandSource> REGISTRY_SUGGESTIONS = (context, builder) ->
		CommandSource.suggestIdentifiers(Registries.ROOT.stream()
			.filter(registry -> !MetatagHolder.of(registry).specter$getMetatags().isEmpty())
			.map(registry -> registry.getKey().getValue()), builder);

	public static final Function<String, SuggestionProvider<ServerCommandSource>> METATAG_SUGGESTIONS = (registryArg) -> (context, builder) -> {
		Registry<?> registry = getRegistryFromContext(context, registryArg);
		if (registry == null) return null;
		return CommandSource.suggestIdentifiers(
			MetatagHolder.of(registry)
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
		return Registries.ROOT.get(context.getArgument(registryArg, Identifier.class));
	}

	private static Metatag<?, ?> getMetatagFromContext(CommandContext<ServerCommandSource> context) {
		Registry<?> registry = getRegistryFromContext(context, "registry");
		Identifier metatagId = context.getArgument("metatag", Identifier.class);
		return MetatagHolder.of(registry).specter$getMetatag(metatagId);
	}

	private static <R, V> int dump(CommandContext<ServerCommandSource> context, Metatag<R, V> metatag) {
		Codec<MetatagResource<V>> codec = MetatagResource.resourceCodecOf(metatag);
		MetatagResource<V> resource = new MetatagResource<>(
			false,
			Streams.stream(metatag.iterator())
				.map(entry -> Pair.of(metatag.registry().getId(entry.key()), entry.value()))
				.toList()
		);

		context.getSource().sendFeedback(() ->
			Text.of(codec.encodeStart(JsonOps.INSTANCE, resource).getOrThrow().toString()), true);
		return Command.SINGLE_SUCCESS;
	}
}
