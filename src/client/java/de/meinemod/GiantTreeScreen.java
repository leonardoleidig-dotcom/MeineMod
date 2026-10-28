package de.meinemod;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class GiantTreeScreen extends Screen {
    private final BlockPos saplingPos;
    private int boneMeal = 1;
    private int boneBlocks = 0;

    public GiantTreeScreen(BlockPos saplingPos) {
        super(Text.literal("Riesenbaum-Zucht"));
        this.saplingPos = saplingPos.toImmutable();
    }

    @Override
    protected void init() {
        int center = this.width / 2;
        int y = this.height / 2 - 35;
        addDrawableChild(ButtonWidget.builder(Text.literal("−"), b -> boneMeal = Math.max(0, boneMeal - 1))
                .dimensions(center - 105, y, 25, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("+"), b -> boneMeal = Math.min(64, boneMeal + 1))
                .dimensions(center + 80, y, 25, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("−"), b -> boneBlocks = Math.max(0, boneBlocks - 1))
                .dimensions(center - 105, y + 28, 25, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("+"), b -> boneBlocks = Math.min(64, boneBlocks + 1))
                .dimensions(center + 80, y + 28, 25, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Riesenbaum wachsen lassen"), b -> {
            ClientPlayNetworking.send(new GrowTreePayload(saplingPos, boneMeal, boneBlocks));
            close();
        }).dimensions(center - 105, y + 62, 210, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Abbrechen"), b -> close())
                .dimensions(center - 45, y + 91, 90, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        int center = this.width / 2;
        int y = this.height / 2 - 62;
        context.drawCenteredTextWithShadow(textRenderer, title, center, y, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.literal("Knochenmehl: " + boneMeal), center - 70, y + 34, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.literal("Knochenblöcke: " + boneBlocks), center - 70, y + 62, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Mehr Material = ein größerer Baum"), center, y + 120, 0xD6E8D0);
    }

    @Override
    public boolean shouldPause() {
        return true;
    }
}
