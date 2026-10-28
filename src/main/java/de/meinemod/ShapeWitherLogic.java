package de.meinemod;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Detects a soul-sand/soul-soil ritual and spawns a chain of Withers for longer builds.
 * Each segment is a real vanilla Wither, so this is a visual/gameplay prototype rather
 * than one custom entity with a genuinely extended model.
 */
public final class ShapeWitherLogic {
    private static final int MAX_COMPONENT_BLOCKS = 512;
    private static final int MAX_SCAN_DISTANCE = 32;
    private static final int MAX_SEGMENTS = 12;
    private static final double SEGMENT_SPACING = 2.15;

    private ShapeWitherLogic() {}

    public static boolean tryActivate(World world, BlockPos skullPos) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) return false;
        if (!isSkull(world.getBlockState(skullPos))) return false;

        BlockPos seed = findAdjacentSoulBlock(world, skullPos);
        if (seed == null) return false;

        List<BlockPos> soulBlocks = collectConnectedSoulBlocks(world, seed);
        if (soulBlocks.size() < 3) return false;

        Set<BlockPos> soulSet = new HashSet<>(soulBlocks);
        Set<BlockPos> skullSet = new HashSet<>();
        for (BlockPos soul : soulBlocks) {
            for (BlockPos nearby : BlockPos.iterate(soul.add(-1, -1, -1), soul.add(1, 1, 1))) {
                if (isSkull(world.getBlockState(nearby))) skullSet.add(nearby.toImmutable());
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

        double centerX = (minX + maxX + 1.0) / 2.0;
        double centerY = (minY + maxY + 1.0) / 2.0;
        double centerZ = (minZ + maxZ + 1.0) / 2.0;
        int spanX = maxX - minX + 1;
        int spanZ = maxZ - minZ + 1;
        boolean alongX = spanX >= spanZ;
        int horizontalLength = Math.max(spanX, spanZ);
        int segments = Math.max(1, Math.min(MAX_SEGMENTS, (horizontalLength + 2) / 3));
        float yaw = alongX ? 90.0F : 0.0F;

        // Consume the ritual before spawning, so the structure cannot activate twice.
        for (BlockPos pos : soulSet) world.breakBlock(pos, false);
        for (BlockPos pos : skullSet) world.breakBlock(pos, false);

        // One vanilla Wither per segment: each contributes its own ribbed body and
        // three heads, creating a longer multi-headed chain for longer horizontal builds.
        for (int i = 0; i < segments; i++) {
            WitherEntity wither = EntityType.WITHER.create(serverWorld);
            if (wither == null) continue;
            double offset = (i - (segments - 1) / 2.0) * SEGMENT_SPACING;
            double x = centerX + (alongX ? offset : 0.0);
            double z = centerZ + (alongX ? 0.0 : offset);
            wither.refreshPositionAndAngles(x, centerY, z, yaw, 0.0F);
            serverWorld.spawnEntity(wither);
        }
        return true;
    }

    private static boolean isSkull(BlockState state) {
        return state.isOf(Blocks.WITHER_SKELETON_SKULL)
                || state.isOf(Blocks.WITHER_SKELETON_WALL_SKULL);
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
