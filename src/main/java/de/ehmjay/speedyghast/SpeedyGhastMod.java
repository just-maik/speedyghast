package de.ehmjay.speedyghast;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeedyGhastMod implements ModInitializer {
	public static final String MOD_ID = "speedyghast";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static SpeedyGhastConfig CONFIG;

	@Override
	public void onInitialize() {
		LOGGER.info("");
		LOGGER.info("\u00a0\u00a0\u00a0\u00a0╭───────────╮");
		LOGGER.info("\u00a0\u00a0\u00a0\u00a0│\u00a0\u00a0○\u00a0\u00a0\u00a0\u00a0\u00a0○\u00a0\u00a0│\u00a0\u00a0═══");
		LOGGER.info("\u00a0\u00a0\u00a0\u00a0│\u00a0\u00a0\u00a0\u00a0\u00a0ᴗ\u00a0\u00a0\u00a0\u00a0\u00a0│\u00a0\u00a0═══");
		LOGGER.info("\u00a0\u00a0\u00a0\u00a0╰───────────╯\u00a0\u00a0═══");
		LOGGER.info("\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0╱\u00a0╱\u00a0╱\u00a0╱");
		LOGGER.info("");\u00a0
		LOGGER.info("\u00a0\u00a0\u00a0\u00a0SpeedyGhast by ehmjay");
		LOGGER.info("");

        // Register Config
        AutoConfig.register(SpeedyGhastConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(SpeedyGhastConfig.class).getConfig();

        // Register Tick Handler
        ServerTickEvents.END_WORLD_TICK.register(SpeedyGhastSpeedHandler::onWorldTick);
        
        // Register Entity Load Handler to reset Ghasts on load (handling unloaded boosted ghasts)
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_LOAD.register(SpeedyGhastSpeedHandler::onEntityLoad);
	}
}
