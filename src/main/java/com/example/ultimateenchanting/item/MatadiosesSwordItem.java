package com.example.ultimateenchanting.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;

public class MatadiosesSwordItem extends SwordItem {
    private final String loreKey;

    public MatadiosesSwordItem(int damage, String loreKey, Properties properties) {
        // Diamond tier + player's base attack are accounted for so the intended
        // base damage values are 7, 10, 13, 16 and 20 before enchantments.
        super(Tiers.DIAMOND, damage - 4, -2.4F, properties);
        this.loreKey = loreKey;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable(loreKey));
    }
}
