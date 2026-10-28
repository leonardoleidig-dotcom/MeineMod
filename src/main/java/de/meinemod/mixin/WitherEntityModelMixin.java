package de.meinemod.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.client.render.entity.state.WitherEntityRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(WitherEntityModel.class)
public abstract class WitherEntityModelMixin {
    @Shadow @Final private ModelPart root;
    @Shadow @Final private ModelPart ribcage;
    @Shadow @Final private ModelPart centerHead;

    @Unique private final List<ModelPart> meineMod$extraRibs = new ArrayList<>();
    @Unique private final List<ModelPart> meineMod$extraHeads = new ArrayList<>();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void meineMod$addExtendedParts(ModelPart root, CallbackInfo ci) {
        Map<String, ModelPart> children = ((ModelPartAccessor) (Object) this.root).meineMod$getChildren();
        List<ModelPart.Cuboid> ribCuboids = ((ModelPartAccessor) (Object) ribcage).meineMod$getCuboids();
        List<ModelPart.Cuboid> headCuboids = ((ModelPartAccessor) (Object) centerHead).meineMod$getCuboids();

        for (int i = 1; i < 12; i++) {
            ModelPart rib = new ModelPart(new ArrayList<>(ribCuboids), new HashMap<>());
            rib.setPivot(0.0F, 6.0F, i * 6.0F);
            rib.visible = false;
            children.put("meine_mod_extra_rib_" + i, rib);
            meineMod$extraRibs.add(rib);

            ModelPart head = new ModelPart(new ArrayList<>(headCuboids), new HashMap<>());
            head.setPivot(0.0F, 4.0F, -8.0F + i * 8.0F);
            head.visible = false;
            children.put("meine_mod_extra_head_" + i, head);
            meineMod$extraHeads.add(head);
        }
    }

    @Inject(method = "setAngles", at = @At("TAIL"))
    private void meineMod$setExtendedPartVisibility(WitherEntityRenderState state, CallbackInfo ci) {
        int segments = state instanceof ShapeWitherRenderStateAccess shapeState
                ? shapeState.meineMod$getSegments() : 1;
        for (int i = 0; i < meineMod$extraRibs.size(); i++) {
            boolean show = i < segments - 1;
            meineMod$extraRibs.get(i).visible = show;
            meineMod$extraHeads.get(i).visible = show;
        }
    }
}
