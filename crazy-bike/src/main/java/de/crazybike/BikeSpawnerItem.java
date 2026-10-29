package de.crazybike;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class BikeSpawnerItem extends Item {
    public BikeSpawnerItem(Settings settings) { super(settings); }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        HitResult hit = user.raycast(6.0, 0.0f, false);
        if (hit.getType() != HitResult.Type.BLOCK) return TypedActionResult.pass(stack);
        if (!world.isClient) {
            CrazyBikeEntity bike = CrazyBikeMod.CRAZY_BIKE.create(world);
            if (bike == null) return TypedActionResult.fail(stack);
            bike.refreshPositionAndAngles(hit.getPos().x, hit.getPos().y + 0.5, hit.getPos().z, user.getYaw(), 0.0f);
            bike.equipSaddle(null);
            world.spawnEntity(bike);
            if (!user.getAbilities().creativeMode) stack.decrement(1);
        }
        return TypedActionResult.success(stack, world.isClient);
    }
}
