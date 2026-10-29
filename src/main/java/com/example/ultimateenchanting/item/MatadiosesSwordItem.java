package com.example.ultimateenchanting.item;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

public class MatadiosesSwordItem extends SwordItem {
    public MatadiosesSwordItem(int damage, Properties properties) {
        // SwordItem adds the tier bonus to the supplied modifier. Subtract four
        // so the player's base 1 damage plus the weapon modifier equals the requested value.
        super(Tiers.DIAMOND, damage - 4, -2.4F, properties);
    }
}
