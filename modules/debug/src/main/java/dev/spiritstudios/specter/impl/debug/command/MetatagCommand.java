package dev.spiritstudios.specter.impl.debug.command;

import java.util.Map;
import java.util.function.Function;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.api.registry.metatag.data.MetatagResource;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagHolder;

public final class MetatagCommand {
	public static final SuggestionProvider<CommandSourceStack> REGISTRY_SUGGESTIONS = (context, builder) ->
			SharedSuggestionProvider.suggestResource(context.getSource().registryAccess().listRegistryKeys()
					.filter(key -> !MetatagHolder.ofAny(key).specter$getMetatags().isEmpty())
					.map(ResourceKey::location), builder);

	public static final Function<String, SuggestionProvider<CommandSourceStack>> METATAG_SUGGESTIONS = (registryArg) -> (context, builder) -> {
		Registry<?> registry = getRegistryFromContext(context, registryArg);
		if (registry == null) return null;
		return SharedSuggestionProvider.suggestResource(
				MetatagHolder.of(registry.key())
						.specter$getMetatags()
						.stream()
						.map(Map.Entry::getKey),
				builder
		);
	};


	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("metatag")
				.requires(source -> source.hasPermission(2))
				.then(Commands.literal("dump")
						.then(Commands.argument("registry", ResourceLocationArgument.id())
								.suggests(REGISTRY_SUGGESTIONS)
								.then(Commands.argument("metatag", ResourceLocationArgument.id())
										.suggests(METATAG_SUGGESTIONS.apply("registry"))
										.executes(context -> dump(
												context,
												getMetatagFromContext(context)
										))
								)
						)
				));
	}

	private static Registry<?> getRegistryFromContext(CommandContext<CommandSourceStack> context, String registryArg) {
		return context.getSource().registryAccess()
				.lookupOrThrow(ResourceKey.createRegistryKey(context.getArgument(registryArg, ResourceLocation.class)));
	}

	private static Metatag<?, ?> getMetatagFromContext(CommandContext<CommandSourceStack> context) {
		Registry<?> registry = getRegistryFromContext(context, "registry");
		ResourceLocation metatagId = context.getArgument("metatag", ResourceLocation.class);
		return MetatagHolder.of(registry.key()).specter$getMetatag(metatagId);
	}

	private static <R, V> int dump(CommandContext<CommandSourceStack> context, Metatag<R, V> metatag) {
		Codec<MetatagResource<R, V>> codec = MetatagResource.resourceCodecOf(metatag);

		RegistryAccess registryManager = context.getSource().registryAccess();
		Registry<R> registry = registryManager.lookupOrThrow(metatag.registryKey());

		MetatagResource<R, V> resource = new MetatagResource<>(
				false,
				metatag.stream()
						.map(entry ->
								Pair.of(registry.getResourceKey(entry.getKey()).orElseThrow(), entry.getValue()))
						.toList()
		);

		context.getSource().sendSuccess(() ->
				NbtUtils.toPrettyComponent(codec.encodeStart(NbtOps.INSTANCE, resource).getOrThrow()), true);
		return Command.SINGLE_SUCCESS;
	}
}
