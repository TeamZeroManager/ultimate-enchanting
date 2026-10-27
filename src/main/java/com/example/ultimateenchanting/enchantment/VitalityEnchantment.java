package com.example.ultimateenchanting.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class VitalityEnchantment extends Enchantment {
    public VitalityEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.ARMOR,
                new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
    }

    @Override public int getMaxLevel() { return 5; }
    @Override public int getMinCost(int level) { return 10 + (level - 1) * 10; }
    @Override public int getMaxCost(int level) { return getMinCost(level) + 15; }
}
