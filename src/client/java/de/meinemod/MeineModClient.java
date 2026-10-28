package de.meinemod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.registry.tag.BlockTags;

public class MeineModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (world.isClient && hand == Hand.MAIN_HAND &&
                    world.getBlockState(hit.getBlockPos()).isIn(BlockTags.SAPLINGS)) {
                MinecraftClient.getInstance().setScreen(new GiantTreeScreen(hit.getBlockPos()));
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
        MeineMod.LOGGER.info("Meine Mod: Client bereit.");
    }
}
