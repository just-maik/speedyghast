package de.ehmjay.speedyghast;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;

public class SpeedyGhastSpeedHandler {

    private static int tickCounter = 0;
    // Track boosted Ghasts per world to reset them when dismounted
    private static final java.util.Map<net.minecraft.registry.RegistryKey<net.minecraft.world.World>, java.util.Set<java.util.UUID>> trackedGhasts = new java.util.concurrent.ConcurrentHashMap<>();

    public static void onWorldTick(ServerWorld world) {
        // Performance optimization: Check only every N ticks
        if (tickCounter++ % SpeedyGhastMod.CONFIG.check_interval != 0) {
            return;
        }

        java.util.Set<java.util.UUID> worldTracked = trackedGhasts.computeIfAbsent(world.getRegistryKey(), k -> new java.util.HashSet<>());
        java.util.Set<java.util.UUID> activeThisTick = new java.util.HashSet<>();

        // Optimization: Iterate players instead of all entities
        for (PlayerEntity player : world.getPlayers()) {
            if (player.getVehicle() instanceof GhastEntity ghast) {
                handleActiveGhast(ghast, player);
                activeThisTick.add(ghast.getUuid());
            }
        }

        // Reset Ghasts that are no longer ridden by a player
        // We use a copy or iterator to safely remove
        worldTracked.removeIf(uuid -> {
            if (!activeThisTick.contains(uuid)) {
                Entity entity = world.getEntity(uuid);
                if (entity instanceof GhastEntity ghast) {
                    resetGhast(ghast);
                }
                // If entity is null (unloaded), we still remove it from tracking.
                // It will be reset via onEntityLoad when it comes back.
                return true;
            }
            return false;
        });

        // Add currently active to tracked
        worldTracked.addAll(activeThisTick);
    }

    public static void onEntityLoad(Entity entity, ServerWorld world) {
        if (entity instanceof GhastEntity ghast) {
            // Ensure fresh state on load
            resetGhast(ghast);
        }
    }

    private static void handleActiveGhast(GhastEntity ghast, PlayerEntity player) {
        EntityAttributeInstance flyingSpeedAttribute = ghast.getAttributeInstance(EntityAttributes.GENERIC_FLYING_SPEED);
        if (flyingSpeedAttribute == null) return;

        double baseSpeed = SpeedyGhastMod.CONFIG.base_speed;
        double newSpeed = baseSpeed;

        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        int soulSpeedLevel = EnchantmentHelper.getLevel(player.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.SOUL_SPEED).get(), boots);

        if (soulSpeedLevel > 0) {
            double multiplier = 1.0;
            if (soulSpeedLevel == 1) multiplier = SpeedyGhastMod.CONFIG.speed_multiplier.level_1;
            else if (soulSpeedLevel == 2) multiplier = SpeedyGhastMod.CONFIG.speed_multiplier.level_2;
            else if (soulSpeedLevel >= 3) multiplier = SpeedyGhastMod.CONFIG.speed_multiplier.level_3;

            newSpeed = baseSpeed * multiplier;
        }

        // Apply speed if changed
        if (Math.abs(flyingSpeedAttribute.getBaseValue() - newSpeed) > 0.0001) {
            flyingSpeedAttribute.setBaseValue(newSpeed);
        }
    }

    private static void resetGhast(GhastEntity ghast) {
        EntityAttributeInstance flyingSpeedAttribute = ghast.getAttributeInstance(EntityAttributes.GENERIC_FLYING_SPEED);
        if (flyingSpeedAttribute != null) {
            flyingSpeedAttribute.setBaseValue(SpeedyGhastMod.CONFIG.base_speed);
        }
    }
}
