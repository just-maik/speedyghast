package de.ehmjay.speedyghast;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // Only provide config screen if Cloth Config is available
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            return ClothConfigScreenFactory::createScreen;
        } else {
            return parent -> null; // No config screen available without Cloth Config
        }
    }
}
