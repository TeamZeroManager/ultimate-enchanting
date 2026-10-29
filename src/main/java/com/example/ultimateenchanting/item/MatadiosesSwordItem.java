package com.example.ultimateenchanting.item;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

public class MatadiosesSwordItem extends SwordItem {
    public MatadiosesSwordItem(int damage, Properties properties) {
        // Tiers.DIAMOND provides familiar durability/mining properties; the chosen
        // modifier makes the final attack attribute equal to the variant's 7-20 damage.
        super(Tiers.DIAMOND, damage - Tiers.DIAMOND.getAttackDamageBonus(), -2.4F, properties);
    }
}
