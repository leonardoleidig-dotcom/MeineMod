package de.meinemod.mixin;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.WitherEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Inject(method = "updateRenderState", at = @At("TAIL"))
    private void meineMod$copyShapeLength(Entity entity, EntityRenderState state, float tickDelta, CallbackInfo ci) {
        if (entity instanceof WitherEntity wither && state instanceof ShapeWitherRenderStateAccess shapeState) {
            int segments = 1;
            if (wither.getCustomName() != null) {
                String name = wither.getCustomName().getString();
                if (name.startsWith("shape_wither:")) {
                    try {
                        segments = Integer.parseInt(name.substring("shape_wither:".length()));
                    } catch (NumberFormatException ignored) {
                        segments = 1;
                    }
                }
            }
            shapeState.meineMod$setSegments(segments);
        }
    }
}
