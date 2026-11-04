package com.example.ultimateenchanting.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

public class KnowledgeEnchantment extends Enchantment {
    public KnowledgeEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override public boolean canEnchant(ItemStack stack) { return stack.getItem() instanceof SwordItem; }

    @Override public int getMaxLevel() { return 1; }
    @Override public int getMinCost(int level) { return 80; }
    @Override public int getMaxCost(int level) { return 120; }
}
