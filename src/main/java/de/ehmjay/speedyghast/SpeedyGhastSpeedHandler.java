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

    public static void onWorldTick(ServerWorld world) {
        // Performance optimization: Check only every N ticks
        if (tickCounter++ % SpeedyGhastMod.CONFIG.check_interval != 0) {
            return;
        }

        // Iterate over all loaded entities in the world
        for (Entity entity : world.iterateEntities()) {
            if (entity instanceof GhastEntity ghast) {
                handleGhast(ghast);
            }
        }
    }

    private static void handleGhast(GhastEntity ghast) {
        EntityAttributeInstance flyingSpeedAttribute = ghast.getAttributeInstance(EntityAttributes.GENERIC_FLYING_SPEED);
        if (flyingSpeedAttribute == null) return;

        double baseSpeed = SpeedyGhastMod.CONFIG.base_speed;
        double newSpeed = baseSpeed;

        if (ghast.hasPassengers() && ghast.getFirstPassenger() instanceof PlayerEntity player) {
            ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
            
            int soulSpeedLevel = EnchantmentHelper.getLevel(player.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.SOUL_SPEED).get(), boots);

            if (soulSpeedLevel > 0) {
                double multiplier = 1.0;
                if (soulSpeedLevel == 1) multiplier = SpeedyGhastMod.CONFIG.speed_multiplier.level_1;
                else if (soulSpeedLevel == 2) multiplier = SpeedyGhastMod.CONFIG.speed_multiplier.level_2;
                else if (soulSpeedLevel >= 3) multiplier = SpeedyGhastMod.CONFIG.speed_multiplier.level_3;

                newSpeed = baseSpeed * multiplier;
            }
        }

        // Apply speed if changed
        if (Math.abs(flyingSpeedAttribute.getBaseValue() - newSpeed) > 0.0001) {
            flyingSpeedAttribute.setBaseValue(newSpeed);
        }
    }
}
