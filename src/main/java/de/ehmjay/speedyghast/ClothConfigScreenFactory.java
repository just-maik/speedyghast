package de.ehmjay.speedyghast;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screen.Screen;

/**
 * Separate class to create config screen using Cloth Config.
 * This class has compile-time dependencies on Cloth Config, but is only loaded at runtime
 * when Cloth Config is available.
 */
public class ClothConfigScreenFactory {
    
    public static Screen createScreen(Screen parent) {
        return AutoConfig.getConfigScreen(SpeedyGhastConfig.class, parent).get();
    }
}
