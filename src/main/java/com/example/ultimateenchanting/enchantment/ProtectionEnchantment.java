package com.example.ultimateenchanting.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class ProtectionEnchantment extends Enchantment {
    public ProtectionEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.ARMOR,
                new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
    }

    @Override public int getMaxLevel() { return 1; }
    @Override public int getMinCost(int level) { return 50; }
    @Override public int getMaxCost(int level) { return 100; }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return other != Enchantments.ALL_DAMAGE_PROTECTION
                && other != Enchantments.FIRE_PROTECTION
                && other != Enchantments.BLAST_PROTECTION
                && other != Enchantments.PROJECTILE_PROTECTION
                && super.checkCompatibility(other);
    }
}
