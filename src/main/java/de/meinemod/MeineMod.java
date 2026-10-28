package de.meinemod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.registry.tag.BlockTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeineMod implements ModInitializer {
    public static final String MOD_ID = "meine_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(GrowTreePayload.ID, GrowTreePayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(GrowTreePayload.ID, (payload, context) ->
                context.server().execute(() -> growTree(context.player(), payload)));
        LOGGER.info("Meine Mod wird geladen!");
    }

    private static void growTree(ServerPlayerEntity player, GrowTreePayload payload) {
        World world = player.getWorld();
        BlockPos base = payload.pos();
        int mealCount = MathHelper.clamp(payload.boneMeal(), 0, 64);
        int boneCount = MathHelper.clamp(payload.boneBlocks(), 0, 64);
        if (mealCount + boneCount == 0 || player.squaredDistanceTo(base.toCenterPos()) > 64.0) return;
        if (!world.getBlockState(base).isIn(BlockTags.SAPLINGS)) return;
        if (countItem(player, Items.BONE_MEAL) < mealCount ||
                countItem(player, Items.BONE_BLOCK) < boneCount) return;

        removeItems(player, Items.BONE_MEAL, mealCount);
        removeItems(player, Items.BONE_BLOCK, boneCount);

        int height = MathHelper.clamp(8 + mealCount / 2 + boneCount * 3, 8, 48);
        int radius = MathHelper.clamp(3 + height / 8, 3, 9);
        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int dy = 0; dy < height; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    pos.set(base.getX() + dx, base.getY() + dy, base.getZ() + dz);
                    BlockState current = world.getBlockState(pos);
                    if (current.isAir() || current.isReplaceable()) {
                        world.setBlockState(pos, Blocks.OAK_LOG.getDefaultState());
                    }
                }
            }
        }

        BlockState leaves = Blocks.OAK_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true);
        int canopyStart = Math.max(3, height - radius * 2);
        for (int dy = canopyStart; dy <= height + radius; dy++) {
            double progress = (double) (dy - canopyStart) / Math.max(1, height + radius - canopyStart);
            int layerRadius = Math.max(1, (int) Math.round(radius * (1.0 - progress * 0.75)));
            for (int dx = -layerRadius; dx <= layerRadius; dx++) {
                for (int dz = -layerRadius; dz <= layerRadius; dz++) {
                    if (dx * dx + dz * dz > layerRadius * layerRadius + 2) continue;
                    pos.set(base.getX() + dx, base.getY() + dy, base.getZ() + dz);
                    BlockState current = world.getBlockState(pos);
                    if (current.isAir() || current.isReplaceable()) {
                        world.setBlockState(pos, leaves);
                    }
                }
            }
        }
        world.setBlockState(base, Blocks.OAK_LOG.getDefaultState());
    }

    private static int countItem(ServerPlayerEntity player, Item item) {
        int total = 0;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(item)) total += stack.getCount();
        }
        return total;
    }

    private static void removeItems(ServerPlayerEntity player, Item item, int amount) {
        int remaining = amount;
        for (int i = 0; i < player.getInventory().size() && remaining > 0; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(item)) {
                int take = Math.min(remaining, stack.getCount());
                stack.decrement(take);
                remaining -= take;
            }
        }
    }
}
