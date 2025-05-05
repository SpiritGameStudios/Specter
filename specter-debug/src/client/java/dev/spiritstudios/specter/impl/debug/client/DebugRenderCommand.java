package dev.spiritstudios.specter.impl.debug.client;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import dev.spiritstudios.specter.impl.core.client.debug.DebugRendererRegistryImpl;

public final class DebugRenderCommand {
	public static final SuggestionProvider<FabricClientCommandSource> DEBUG_RENDERER_SUGGESTIONS = (context, builder) ->
			CommandSource.suggestIdentifiers(DebugRendererRegistryImpl.getRenderers().keySet(), builder);

	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(ClientCommandManager.literal("debugrender")
				.then(ClientCommandManager.argument("renderer", IdentifierArgumentType.identifier())
						.suggests(DEBUG_RENDERER_SUGGESTIONS)
						.executes(context -> {
							Identifier id = context.getArgument("renderer", Identifier.class);
							DebugRendererRegistryImpl.getRenderers().get(id).toggle();

							return Command.SINGLE_SUCCESS;
						})
				));
	}
}
