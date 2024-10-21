package dev.spiritstudios.specter.mixin.debug.client;

import dev.spiritstudios.specter.impl.core.debug.DebugRendererRegistryImpl;
import dev.spiritstudios.specter.impl.core.debug.ToggleableDebugRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {
	@Shadow
	@Final
	public DebugRenderer.Renderer waterDebugRenderer;

	@Shadow
	@Final
	public DebugRenderer.Renderer heightmapDebugRenderer;

	@Shadow
	@Final
	public DebugRenderer.Renderer collisionDebugRenderer;

	@Shadow
	@Final
	public DebugRenderer.Renderer supportingBlockDebugRenderer;

	@Shadow
	@Final
	public DebugRenderer.Renderer neighborUpdateDebugRenderer;

	@Shadow
	@Final
	public StructureDebugRenderer structureDebugRenderer;

	@Shadow
	@Final
	public DebugRenderer.Renderer skyLightDebugRenderer;

	@Shadow
	@Final
	public DebugRenderer.Renderer blockOutlineDebugRenderer;

	@Shadow
	@Final
	public DebugRenderer.Renderer chunkLoadingDebugRenderer;

	@Shadow
	@Final
	public VillageDebugRenderer villageDebugRenderer;

	@Shadow
	@Final
	public VillageSectionsDebugRenderer villageSectionsDebugRenderer;

	@Shadow
	@Final
	public BeeDebugRenderer beeDebugRenderer;

	@Shadow
	@Final
	public RaidCenterDebugRenderer raidCenterDebugRenderer;

	@Shadow
	@Final
	public GoalSelectorDebugRenderer goalSelectorDebugRenderer;

	@Shadow
	@Final
	public GameEventDebugRenderer gameEventDebugRenderer;

	@Shadow
	@Final
	public LightDebugRenderer lightDebugRenderer;

	@Shadow
	@Final
	public BreezeDebugRenderer breezeDebugRenderer;

	@Shadow
	@Final
	public PathfindingDebugRenderer pathfindingDebugRenderer;

	@Inject(method = "render", at = @At("RETURN"))
	private void render(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
		DebugRendererRegistryImpl.getRenderers().values()
			.forEach(entry -> entry.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ));
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(MinecraftClient client, CallbackInfo ci) {
		DebugRendererRegistryImpl.register(Identifier.ofVanilla("pathfinding"), new ToggleableDebugRenderer(pathfindingDebugRenderer));

		DebugRendererRegistryImpl.register(Identifier.ofVanilla("water"), new ToggleableDebugRenderer(waterDebugRenderer));

		DebugRendererRegistryImpl.register(Identifier.ofVanilla("heightmap"), new ToggleableDebugRenderer(heightmapDebugRenderer));

		DebugRendererRegistryImpl.register(Identifier.ofVanilla("collision"), new ToggleableDebugRenderer(collisionDebugRenderer));

		DebugRendererRegistryImpl.register(Identifier.ofVanilla("supporting_block"), new ToggleableDebugRenderer(supportingBlockDebugRenderer));

		DebugRendererRegistryImpl.register(Identifier.ofVanilla("neighbor_update"), new ToggleableDebugRenderer(neighborUpdateDebugRenderer));

		DebugRendererRegistryImpl.register(Identifier.ofVanilla("structure"), new ToggleableDebugRenderer(structureDebugRenderer));

		DebugRendererRegistryImpl.register(Identifier.ofVanilla("sky_light"), new ToggleableDebugRenderer(skyLightDebugRenderer));

		DebugRendererRegistryImpl.register(Identifier.ofVanilla("block_outline"), new ToggleableDebugRenderer(blockOutlineDebugRenderer));

		DebugRendererRegistryImpl.register(Identifier.ofVanilla("chunk_loading"), new ToggleableDebugRenderer(chunkLoadingDebugRenderer));

		DebugRendererRegistryImpl.register(Identifier.ofVanilla("village"), new ToggleableDebugRenderer(villageDebugRenderer));

		DebugRendererRegistryImpl.register(Identifier.ofVanilla("village_sections"), new ToggleableDebugRenderer(villageSectionsDebugRenderer));

		DebugRendererRegistryImpl.register(Identifier.ofVanilla("bee"), new ToggleableDebugRenderer(beeDebugRenderer));

		DebugRendererRegistryImpl.register(Identifier.ofVanilla("raid_center"), new ToggleableDebugRenderer(raidCenterDebugRenderer));

		DebugRendererRegistryImpl.register(Identifier.ofVanilla("goal_selector"), new ToggleableDebugRenderer(goalSelectorDebugRenderer));

		DebugRendererRegistryImpl.register(Identifier.ofVanilla("game_event"), new ToggleableDebugRenderer(gameEventDebugRenderer));

		DebugRendererRegistryImpl.register(Identifier.ofVanilla("light"), new ToggleableDebugRenderer(lightDebugRenderer));

		DebugRendererRegistryImpl.register(
			Identifier.ofVanilla("breeze"),
			new ToggleableDebugRenderer((matrices, vertexConsumers, cameraX, cameraY, cameraZ) -> breezeDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ))
		);
	}
}