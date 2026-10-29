package de.crazybike;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrazyBikeMod implements ModInitializer {
    public static final String MOD_ID = "crazy_bike";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final EntityType<CrazyBikeEntity> CRAZY_BIKE = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier.of(MOD_ID, "crazy_bike"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CrazyBikeEntity::new)
            .dimensions(EntityDimensions.fixed(1.25f, 0.9f))
            .trackRangeBlocks(10)
            .trackedUpdateRate(2)
            .build()
    );

    public static final Item BIKE_SPAWNER = Registry.register(
        Registries.ITEM,
        Identifier.of(MOD_ID, "bike_spawner"),
        new BikeSpawnerItem(new Item.Settings().maxCount(1))
    );

    @Override
    public void onInitialize() {
        LOGGER.info("Crazy Bike geladen! Bike-Spawner: crazy_bike:bike_spawner");
    }
}
