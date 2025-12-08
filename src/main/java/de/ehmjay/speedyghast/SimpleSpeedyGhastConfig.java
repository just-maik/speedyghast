package de.ehmjay.speedyghast;

/**
 * Simple POJO configuration class that doesn't depend on Cloth Config.
 * Used as a fallback when Cloth Config is not available.
 */
public class SimpleSpeedyGhastConfig implements ISpeedyGhastConfig {
    
    public double base_speed = 0.05;
    public SpeedMultiplier speed_multiplier = new SpeedMultiplier();
    public int check_interval = 20;

    @Override
    public double getBaseSpeed() {
        return base_speed;
    }

    @Override
    public SpeedMultiplierConfig getSpeedMultiplier() {
        return speed_multiplier;
    }

    @Override
    public int getCheckInterval() {
        return check_interval;
    }

    public static class SpeedMultiplier implements ISpeedyGhastConfig.SpeedMultiplierConfig {
        public double level_1 = 1.5;
        public double level_2 = 2.0;
        public double level_3 = 2.5;

        @Override
        public double getLevel1() {
            return level_1;
        }

        @Override
        public double getLevel2() {
            return level_2;
        }

        @Override
        public double getLevel3() {
            return level_3;
        }
    }
}
