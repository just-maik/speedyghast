package de.ehmjay.speedyghast;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

/**
 * Separate class to load Cloth Config.
 * This class has compile-time dependencies on Cloth Config, but is only loaded at runtime
 * when Cloth Config is available.
 */
public class ClothConfigLoader {
    
    public static ISpeedyGhastConfig load() {
        AutoConfig.register(SpeedyGhastConfig.class, GsonConfigSerializer::new);
        return AutoConfig.getConfigHolder(SpeedyGhastConfig.class).getConfig();
    }
}
