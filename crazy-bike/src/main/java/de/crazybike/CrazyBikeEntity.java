package de.crazybike;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class CrazyBikeEntity extends PigEntity {
    public CrazyBikeEntity(EntityType<? extends PigEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
        // A short turbo burst can be triggered with the normal carrot-on-a-stick control.
        if (this.getControllingPassenger() != null && this.isSprinting()) {
            this.addVelocity(this.getRotationVector().multiply(0.035, 0.0, 0.035));
            this.velocityDirty = true;
        }
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }
}
