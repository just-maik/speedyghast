package de.ehmjay.speedyghast;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class SpeedyGhastMod implements ModInitializer {
	public static final String MOD_ID = "speedyghast";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static ISpeedyGhastConfig CONFIG;

	@Override
	public void onInitialize() {
		LOGGER.info("");
		LOGGER.info("\u00a0\u00a0\u00a0\u00a0╭───────────╮");
		LOGGER.info("\u00a0\u00a0\u00a0\u00a0│\u00a0\u00a0○\u00a0\u00a0\u00a0\u00a0\u00a0○\u00a0\u00a0│\u00a0\u00a0");
		LOGGER.info("\u00a0\u00a0\u00a0\u00a0│\u00a0\u00a0\u00a0\u00a0\u00a0ᴗ\u00a0\u00a0\u00a0\u00a0\u00a0│\u00a0\u00a0");
		LOGGER.info("\u00a0\u00a0\u00a0\u00a0╰───────────╯\u00a0\u00a0");
		LOGGER.info("\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0╱\u00a0╱\u00a0╱\u00a0╱");
		LOGGER.info("");
		LOGGER.info("\u00a0\u00a0\u00a0\u00a0 Loaded SpeedyGhast by ehmjay!");
		LOGGER.info("");

        // Register Config - check if Cloth Config is available
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            LOGGER.info("Cloth Config detected - using AutoConfig");
            try {
                CONFIG = ClothConfigLoader.load();
            } catch (Exception e) {
                LOGGER.error("Failed to load Cloth Config, falling back to simple config", e);
                CONFIG = loadSimpleConfig();
            }
        } else {
            LOGGER.info("Cloth Config not detected - using simple JSON config");
            CONFIG = loadSimpleConfig();
        }

        // Register Tick Handler
        ServerTickEvents.END_WORLD_TICK.register(SpeedyGhastSpeedHandler::onWorldTick);
        
        // Register Entity Load Handler to reset Ghasts on load (handling unloaded boosted ghasts)
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_LOAD.register(SpeedyGhastSpeedHandler::onEntityLoad);
	}

    /**
     * Loads config using the simple Gson-based loader.
     */
    private static ISpeedyGhastConfig loadSimpleConfig() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("speedyghast.json");
        SimpleConfigLoader loader = new SimpleConfigLoader(configPath);
        return loader.load();
    }
}
