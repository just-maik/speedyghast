package de.ehmjay.speedyghast;

/**
 * Common interface for both Cloth Config and simple config implementations.
 * This allows the rest of the code to work with either config system.
 */
public interface ISpeedyGhastConfig {
    double getBaseSpeed();
    SpeedMultiplierConfig getSpeedMultiplier();
    int getCheckInterval();
    
    interface SpeedMultiplierConfig {
        double getLevel1();
        double getLevel2();
        double getLevel3();
    }
}
