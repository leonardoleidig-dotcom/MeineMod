package de.crazybike;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class CrazyBikeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(CrazyBikeMod.CRAZY_BIKE, CrazyBikeRenderer::new);
    }

    private static class CrazyBikeRenderer extends EntityRenderer<CrazyBikeEntity> {
        private final BlockRenderManager blocks;

        CrazyBikeRenderer(EntityRendererFactory.Context context) {
            super(context);
            this.blocks = context.getBlockRenderManager();
            this.shadowRadius = 0.55f;
        }

        @Override
        public void render(CrazyBikeEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                           VertexConsumerProvider consumers, int light) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - entity.getYaw(tickDelta)));
            // Blocky custom bike silhouette: two dark wheels, a bright body, and handlebars.
            drawBlock(matrices, consumers, Blocks.BLACK_CONCRETE, -0.35f, 0.10f, -0.42f, 0.22f, 0.22f, 0.22f, light);
            drawBlock(matrices, consumers, Blocks.BLACK_CONCRETE, -0.35f, 0.10f, 0.34f, 0.22f, 0.22f, 0.22f, light);
            drawBlock(matrices, consumers, Blocks.RED_CONCRETE, -0.28f, 0.28f, -0.28f, 0.56f, 0.20f, 0.62f, light);
            drawBlock(matrices, consumers, Blocks.LIGHT_GRAY_CONCRETE, -0.12f, 0.48f, -0.10f, 0.24f, 0.12f, 0.30f, light);
            drawBlock(matrices, consumers, Blocks.YELLOW_CONCRETE, -0.08f, 0.32f, -0.50f, 0.16f, 0.12f, 0.10f, light);
            matrices.pop();
            super.render(entity, yaw, tickDelta, matrices, consumers, light);
        }

        private void drawBlock(MatrixStack matrices, VertexConsumerProvider consumers,
                               net.minecraft.block.Block block, float x, float y, float z,
                               float sx, float sy, float sz, int light) {
            matrices.push();
            matrices.translate(x, y, z);
            matrices.scale(sx, sy, sz);
            blocks.renderBlockAsEntity(block.getDefaultState(), matrices, consumers, light, 0);
            matrices.pop();
        }

        @Override
        public Identifier getTexture(CrazyBikeEntity entity) {
            return Identifier.of("minecraft", "textures/block/red_concrete.png");
        }
    }
}
