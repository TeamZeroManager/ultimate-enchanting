package com.example.ultimateenchanting.event;

import com.example.ultimateenchanting.UltimateEnchanting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MatadiosesHandler {
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        Entity attacker = event.getSource().getEntity();
        LivingEntity target = event.getEntity();
        if (!(attacker instanceof Player player) || !(target instanceof Warden)) return;

        ItemStack weapon = player.getMainHandItem();
        if (UltimateEnchanting.MATADIOSES_ITEMS.stream().noneMatch(item -> weapon.is(item.get()))) return;

        // Always deal 100 damage to the Warden, independent of enchantment bonuses.
        event.setAmount(100.0F);
    }
}
