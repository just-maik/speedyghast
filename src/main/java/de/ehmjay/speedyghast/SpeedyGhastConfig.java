package de.ehmjay.speedyghast;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "speedyghast")
public class SpeedyGhastConfig implements ConfigData {
    
    @ConfigEntry.Gui.Tooltip
    public double base_speed = 0.05;

    @ConfigEntry.Gui.CollapsibleObject
    public SpeedMultiplier speed_multiplier = new SpeedMultiplier();

    @ConfigEntry.Gui.Tooltip
    public int check_interval = 20;

    public static class SpeedMultiplier {
        public double level_1 = 1.5;
        public double level_2 = 2.0;
        public double level_3 = 2.5;
    }
}
