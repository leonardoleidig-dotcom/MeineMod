package de.meinemod;

import net.fabricmc.api.ClientModInitializer;

public class MeineModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MeineMod.LOGGER.info("Meine Mod: Client bereit.");
    }
}
