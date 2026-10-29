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

    // Register these during onInitialize, rather than when this class is loaded.
    public static EntityType<CrazyBikeEntity> CRAZY_BIKE;
    public static Item BIKE_SPAWNER;

    @Override
    public void onInitialize() {
        CRAZY_BIKE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "crazy_bike"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CrazyBikeEntity::new)
                .dimensions(EntityDimensions.fixed(1.25f, 0.9f))
                .trackRangeBlocks(10)
                .trackedUpdateRate(2)
                .build()
        );

        BIKE_SPAWNER = Registry.register(
            Registries.ITEM,
            Identifier.of(MOD_ID, "bike_spawner"),
            new BikeSpawnerItem(new Item.Settings().maxCount(1))
        );

        LOGGER.info("Crazy Bike geladen! Registrierte Entität: {}, Bike-Spawner: {}",
            Registries.ENTITY_TYPE.getId(CRAZY_BIKE),
            Registries.ITEM.getId(BIKE_SPAWNER));
    }
}
