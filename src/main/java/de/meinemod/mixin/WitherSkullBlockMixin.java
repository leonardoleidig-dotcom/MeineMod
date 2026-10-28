package de.meinemod.mixin;

import de.meinemod.ShapeWitherLogic;
import net.minecraft.block.BlockState;
import net.minecraft.block.WitherSkullBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherSkullBlock.class)
public abstract class WitherSkullBlockMixin {
    @Inject(method = "onPlaced", at = @At("HEAD"), cancellable = true)
    private void meineMod$activateCustomShape(World world, BlockPos pos, BlockState state,
                                               LivingEntity placer, ItemStack stack, CallbackInfo ci) {
        if (ShapeWitherLogic.tryActivate(world, pos)) {
            ci.cancel();
        }
    }
}
