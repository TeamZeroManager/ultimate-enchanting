package com.example.ultimateenchanting.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantments;

public class DamageEnchantment extends Enchantment {
    public DamageEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override public boolean canEnchant(ItemStack stack) { return stack.getItem() instanceof SwordItem; }

    @Override public int getMaxLevel() { return 1; }
    @Override public int getMinCost(int level) { return 50; }
    @Override public int getMaxCost(int level) { return 100; }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return other != Enchantments.SHARPNESS
                && other != Enchantments.SMITE
                && other != Enchantments.BANE_OF_ARTHROPODS
                && super.checkCompatibility(other);
    }
}
