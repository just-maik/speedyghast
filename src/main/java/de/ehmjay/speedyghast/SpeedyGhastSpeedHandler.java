package de.ehmjay.speedyghast;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
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
 * <p>Optimized for multiplayer by:
 * <ul>
 *   <li>Iterating only players (O(players)) instead of all entities</li>
 *   <li>Using tick interval to reduce check frequency</li>
 *   <li>Tracking boosted entities per-world to handle resets efficiently</li>
 * </ul>
 */
public class SpeedyGhastSpeedHandler {

    private static int tickCounter = 0;

    // Track boosted entities per world to reset them when dismounted
    // Using ConcurrentHashMap for thread safety in case of async world ticks
    private static final Map<RegistryKey<World>, Set<UUID>> boostedEntities = new ConcurrentHashMap<>();

    /**
     * Called every world tick. Checks all players for ghast riding and applies speed boosts.
     */
    public static void onWorldTick(ServerWorld world) {
        // Performance: Only check every N ticks (configurable)
        if (++tickCounter % SpeedyGhastMod.CONFIG.check_interval != 0) {
            return;
        }

        Set<UUID> worldBoosted = boostedEntities.computeIfAbsent(world.getRegistryKey(), k -> ConcurrentHashMap.newKeySet());
        Set<UUID> activeThisTick = new HashSet<>();

        // O(players) - iterate only online players in this world
        for (ServerPlayerEntity player : world.getPlayers()) {
            Entity vehicle = player.getVehicle();

            if (vehicle == null) continue;
            if (!(vehicle instanceof LivingEntity livingVehicle)) continue;
            if (!isGhastLike(vehicle)) continue;

            // Player is riding a ghast-like entity
            applySpeedBoost(livingVehicle, player);
            activeThisTick.add(vehicle.getUuid());
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
        if (entity instanceof LivingEntity living && isGhastLike(entity)) {
            resetSpeed(living);
        }
    }

    /**
     * Determines if an entity is a ghast or ghast-like (e.g., Happy Ghast from mods).
     * 
     * <p>Checks:
     * <ul>
     *   <li>Is it a vanilla GhastEntity?</li>
     *   <li>Does its entity type name contain "ghast"?</li>
     * </ul>
     */
    private static boolean isGhastLike(Entity entity) {
        // Vanilla ghast
        if (entity instanceof GhastEntity) {
            return true;
        }

        // Modded ghasts (like Happy Ghast) - check entity type name
        String typeName = entity.getType().getUntranslatedName().toLowerCase();
        return typeName.contains("ghast");
    }

    /**
     * Applies speed boost based on player's Soul Speed enchantment level.
     */
    private static void applySpeedBoost(LivingEntity ghast, PlayerEntity player) {
        EntityAttributeInstance flyingSpeed = ghast.getAttributeInstance(EntityAttributes.GENERIC_FLYING_SPEED);
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
            return EnchantmentHelper.getLevel(
                player.getWorld().getRegistryManager()
                    .get(RegistryKeys.ENCHANTMENT)
                    .getEntry(Enchantments.SOUL_SPEED)
                    .get(),
                boots
            );
        } catch (Exception e) {
            // Fallback if enchantment lookup fails
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
        EntityAttributeInstance flyingSpeed = ghast.getAttributeInstance(EntityAttributes.GENERIC_FLYING_SPEED);
        if (flyingSpeed != null) {
            flyingSpeed.setBaseValue(SpeedyGhastMod.CONFIG.base_speed);
        }
    }
}
