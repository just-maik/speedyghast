package de.ehmjay.speedyghast;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles the speed boost logic for Ghast-like entities when ridden by players
 * wearing Soul Speed boots.
 * 
 * <p>Supports:
 * <ul>
 *   <li>Vanilla GhastEntity</li>
 *   <li>Happy Ghast (minecraft:happy_ghast) - Official mob added in 1.21.6</li>
 * </ul>
 * 
 * <p>For Happy Ghast (which can carry up to 4 players), only the controlling/steering
 * player's Soul Speed boots affect the speed.
 */
public class SpeedyGhastSpeedHandler {

    private static int tickCounter = 0;

    // Track boosted entities per world to reset them when dismounted
    private static final Map<RegistryKey<World>, Set<UUID>> boostedEntities = new ConcurrentHashMap<>();

    /**
     * Called every world tick. Checks all ghast-like entities for riders and applies speed boosts.
     */
    public static void onWorldTick(ServerWorld world) {
        // Performance: Only check every N ticks (configurable)
        if (++tickCounter % SpeedyGhastMod.CONFIG.check_interval != 0) {
            return;
        }

        Set<UUID> worldBoosted = boostedEntities.computeIfAbsent(world.getRegistryKey(), k -> ConcurrentHashMap.newKeySet());
        Set<UUID> activeThisTick = new HashSet<>();

        // Check all players and find those riding ghast-like entities
        for (ServerPlayerEntity player : world.getPlayers()) {
            Entity vehicle = player.getVehicle();
            if (vehicle == null) continue;
            
            // Check if riding a ghast-like entity
            LivingEntity ghast = getGhastEntity(vehicle);
            if (ghast == null) continue;

            // Get the controlling player (the one who steers)
            PlayerEntity controllingPlayer = getControllingPlayer(ghast);
            if (controllingPlayer == null) continue;

            // Only the controlling player's boots matter
            if (player.equals(controllingPlayer)) {
                applySpeedBoost(ghast, controllingPlayer);
                activeThisTick.add(ghast.getUuid());
            }
        }

        // Reset entities that are no longer actively boosted
        for (UUID uuid : worldBoosted) {
            if (!activeThisTick.contains(uuid)) {
                Entity entity = world.getEntity(uuid);
                if (entity instanceof LivingEntity living) {
                    resetSpeed(living);
                }
            }
        }

        // Update tracking set
        worldBoosted.clear();
        worldBoosted.addAll(activeThisTick);
    }

    /**
     * Called when an entity loads into the world.
     * Ensures ghasts don't retain boosted speed from previous sessions.
     */
    public static void onEntityLoad(Entity entity, ServerWorld world) {
        LivingEntity ghast = getGhastEntity(entity);
        if (ghast != null) {
            resetSpeed(ghast);
        }
    }

    /**
     * Returns the entity as a LivingEntity if it's a ghast-like entity, null otherwise.
     * Supports vanilla GhastEntity and Happy Ghast.
     */
    private static LivingEntity getGhastEntity(Entity entity) {
        // Happy Ghast (official 1.21.6 mob)
        if (entity instanceof HappyGhastEntity happyGhast) {
            return happyGhast;
        }
        
        // Vanilla Ghast
        if (entity instanceof GhastEntity ghast) {
            return ghast;
        }

        return null;
    }

    /**
     * Gets the controlling/steering player from a ghast-like entity.
     * For Happy Ghast, this is the player in the first seat who controls movement.
     */
    private static PlayerEntity getControllingPlayer(LivingEntity ghast) {
        // getControllingPassenger() returns the entity that controls movement
        Entity controller = ghast.getControllingPassenger();
        
        if (controller instanceof PlayerEntity player) {
            return player;
        }
        
        // Fallback: check first passenger
        if (!ghast.getPassengerList().isEmpty()) {
            Entity firstPassenger = ghast.getFirstPassenger();
            if (firstPassenger instanceof PlayerEntity player) {
                return player;
            }
        }
        
        return null;
    }

    /**
     * Applies speed boost based on player's Soul Speed enchantment level.
     */
    private static void applySpeedBoost(LivingEntity ghast, PlayerEntity player) {
        // In 1.21.6, EntityAttributes uses RegistryEntry, access via FLYING_SPEED
        EntityAttributeInstance flyingSpeed = ghast.getAttributeInstance(EntityAttributes.FLYING_SPEED);
        if (flyingSpeed == null) return;

        double baseSpeed = SpeedyGhastMod.CONFIG.base_speed;
        double targetSpeed = baseSpeed;

        // Get Soul Speed level from boots
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        int soulSpeedLevel = getSoulSpeedLevel(player, boots);

        if (soulSpeedLevel > 0) {
            double multiplier = getMultiplierForLevel(soulSpeedLevel);
            targetSpeed = baseSpeed * multiplier;
        }

        // Only update if changed (avoid unnecessary attribute updates)
        if (Math.abs(flyingSpeed.getBaseValue() - targetSpeed) > 0.0001) {
            flyingSpeed.setBaseValue(targetSpeed);
        }
    }

    /**
     * Gets the Soul Speed enchantment level from boots.
     */
    private static int getSoulSpeedLevel(PlayerEntity player, ItemStack boots) {
        if (boots.isEmpty()) return 0;

        try {
            // Cast to ServerPlayerEntity since this code only runs server-side
            // This provides a more stable API for accessing the registry manager
            if (!(player instanceof ServerPlayerEntity serverPlayer)) {
                SpeedyGhastMod.LOGGER.warn("getSoulSpeedLevel called with non-ServerPlayerEntity: {}", player.getClass().getName());
                return 0;
            }
            
            var registryManager = serverPlayer.getServerWorld().getRegistryManager();
            var enchantmentRegistry = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT);
            var soulSpeedEntry = enchantmentRegistry.getOptional(Enchantments.SOUL_SPEED);
            
            if (soulSpeedEntry.isPresent()) {
                return EnchantmentHelper.getLevel(soulSpeedEntry.get(), boots);
            }
            return 0;
        } catch (Exception e) {
            // Fallback if enchantment lookup fails
            SpeedyGhastMod.LOGGER.warn("Failed to get Soul Speed level", e);
            return 0;
        }
    }

    /**
     * Gets the speed multiplier for a given Soul Speed level.
     */
    private static double getMultiplierForLevel(int level) {
        return switch (level) {
            case 1 -> SpeedyGhastMod.CONFIG.speed_multiplier.level_1;
            case 2 -> SpeedyGhastMod.CONFIG.speed_multiplier.level_2;
            default -> SpeedyGhastMod.CONFIG.speed_multiplier.level_3; // 3 or higher
        };
    }

    /**
     * Resets a ghast's flying speed to the configured base speed.
     */
    private static void resetSpeed(LivingEntity ghast) {
        EntityAttributeInstance flyingSpeed = ghast.getAttributeInstance(EntityAttributes.FLYING_SPEED);
        if (flyingSpeed != null) {
            flyingSpeed.setBaseValue(SpeedyGhastMod.CONFIG.base_speed);
        }
    }
}
