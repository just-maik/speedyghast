package de.ehmjay.speedyghast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Simple Gson-based config loader used when Cloth Config is not available.
 * Reads and writes config to config/speedyghast.json
 */
public class SimpleConfigLoader {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path configPath;
    
    public SimpleConfigLoader(Path configPath) {
        this.configPath = configPath;
    }
    
    /**
     * Loads the config from file, or creates a default config if the file doesn't exist.
     */
    public SimpleSpeedyGhastConfig load() {
        try {
            if (Files.exists(configPath)) {
                String json = Files.readString(configPath);
                return GSON.fromJson(json, SimpleSpeedyGhastConfig.class);
            } else {
                // Create default config
                SimpleSpeedyGhastConfig defaultConfig = new SimpleSpeedyGhastConfig();
                save(defaultConfig);
                return defaultConfig;
            }
        } catch (IOException e) {
            SpeedyGhastMod.LOGGER.error("Failed to load config from {}, using defaults", configPath, e);
            return new SimpleSpeedyGhastConfig();
        }
    }
    
    /**
     * Saves the config to file.
     */
    public void save(SimpleSpeedyGhastConfig config) {
        try {
            // Ensure parent directory exists
            Files.createDirectories(configPath.getParent());
            
            String json = GSON.toJson(config);
            Files.writeString(configPath, json);
            SpeedyGhastMod.LOGGER.info("Saved config to {}", configPath);
        } catch (IOException e) {
            SpeedyGhastMod.LOGGER.error("Failed to save config to {}", configPath, e);
        }
    }
}
