package de.meinemod;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Finds a connected soul-sand/soul-soil shape and activates it when a skull is placed.
 * The current prototype uses the vanilla Wither entity; the shape determines its spawn
 * location and the structure is consumed.
 */
public final class ShapeWitherLogic {
    private static final int MAX_COMPONENT_BLOCKS = 512;
    private static final int MAX_SCAN_DISTANCE = 32;

    private ShapeWitherLogic() {}

    public static boolean tryActivate(World world, BlockPos skullPos) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) return false;
        if (!world.getBlockState(skullPos).isOf(Blocks.WITHER_SKELETON_SKULL)
                && !world.getBlockState(skullPos).isOf(Blocks.WITHER_SKELETON_WALL_SKULL)) {
            return false;
        }

        BlockPos seed = findAdjacentSoulBlock(world, skullPos);
        if (seed == null) return false;

        List<BlockPos> soulBlocks = collectConnectedSoulBlocks(world, seed);
        if (soulBlocks.size() < 3) return false;

        Set<BlockPos> soulSet = new HashSet<>(soulBlocks);
        Set<BlockPos> skullSet = new HashSet<>();
        for (BlockPos soul : soulBlocks) {
            for (BlockPos nearby : BlockPos.iterate(soul.add(-1, -1, -1), soul.add(1, 1, 1))) {
                BlockState state = world.getBlockState(nearby);
                if (state.isOf(Blocks.WITHER_SKELETON_SKULL)
                        || state.isOf(Blocks.WITHER_SKELETON_WALL_SKULL)) {
                    skullSet.add(nearby.toImmutable());
                }
            }
        }
        if (!skullSet.contains(skullPos) || skullSet.size() < 3) return false;

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (BlockPos pos : soulBlocks) {
            minX = Math.min(minX, pos.getX()); minY = Math.min(minY, pos.getY()); minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX()); maxY = Math.max(maxY, pos.getY()); maxZ = Math.max(maxZ, pos.getZ());
        }
        for (BlockPos pos : skullSet) {
            minX = Math.min(minX, pos.getX()); minY = Math.min(minY, pos.getY()); minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX()); maxY = Math.max(maxY, pos.getY()); maxZ = Math.max(maxZ, pos.getZ());
        }

        // Remove only the blocks that form this connected ritual.
        for (BlockPos pos : soulSet) world.breakBlock(pos, false);
        for (BlockPos pos : skullSet) world.breakBlock(pos, false);

        WitherEntity wither = EntityType.WITHER.create(serverWorld, SpawnReason.TRIGGERED);
        if (wither == null) return true;
        double centerX = (minX + maxX + 1.0) / 2.0;
        double centerY = (minY + maxY + 1.0) / 2.0;
        double centerZ = (minZ + maxZ + 1.0) / 2.0;
        wither.refreshPositionAndAngles(centerX, centerY, centerZ, 0.0F, 0.0F);
        serverWorld.spawnEntity(wither);
        return true;
    }

    private static BlockPos findAdjacentSoulBlock(World world, BlockPos skullPos) {
        for (BlockPos pos : BlockPos.iterate(skullPos.add(-1, -1, -1), skullPos.add(1, 1, 1))) {
            if (isSoulBlock(world.getBlockState(pos))) return pos.toImmutable();
        }
        return null;
    }

    private static List<BlockPos> collectConnectedSoulBlocks(World world, BlockPos seed) {
        List<BlockPos> result = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(seed.toImmutable());
        visited.add(seed.toImmutable());
        while (!queue.isEmpty() && result.size() < MAX_COMPONENT_BLOCKS) {
            BlockPos pos = queue.removeFirst();
            if (!isSoulBlock(world.getBlockState(pos))) continue;
            result.add(pos);
            for (BlockPos next : BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
                BlockPos immutable = next.toImmutable();
                if (visited.contains(immutable) || Math.abs(immutable.getX() - seed.getX()) > MAX_SCAN_DISTANCE
                        || Math.abs(immutable.getY() - seed.getY()) > MAX_SCAN_DISTANCE
                        || Math.abs(immutable.getZ() - seed.getZ()) > MAX_SCAN_DISTANCE) continue;
                if (isSoulBlock(world.getBlockState(immutable))) {
                    visited.add(immutable);
                    queue.addLast(immutable);
                }
            }
        }
        return result;
    }

    private static boolean isSoulBlock(BlockState state) {
        return state.isOf(Blocks.SOUL_SAND) || state.isOf(Blocks.SOUL_SOIL);
    }
}
