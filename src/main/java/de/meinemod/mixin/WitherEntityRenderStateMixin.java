package de.meinemod.mixin;

import net.minecraft.client.render.entity.state.WitherEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WitherEntityRenderState.class)
public abstract class WitherEntityRenderStateMixin implements ShapeWitherRenderStateAccess {
    @Unique
    private int meineMod$segments = 1;

    @Override
    public int meineMod$getSegments() {
        return meineMod$segments;
    }

    @Override
    public void meineMod$setSegments(int segments) {
        meineMod$segments = Math.max(1, Math.min(12, segments));
    }
}
