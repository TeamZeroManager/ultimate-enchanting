package com.example.ultimateenchanting.event;

import com.example.ultimateenchanting.UltimateEnchanting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SuperiorEnchantmentHandler {
    private static final float DAMAGE_XX_BASE_BONUS = 10.5F;
    private static final float DAMAGE_XX_SPECIAL_BONUS = 50.0F;

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();

        if (event.getSource().getEntity() instanceof Player attacker) {
            ItemStack weapon = attacker.getMainHandItem();
            int damageLevel = EnchantmentHelper.getItemEnchantmentLevel(UltimateEnchanting.DAMAGE.get(), weapon);
            if (damageLevel > 0) {
                float bonus = DAMAGE_XX_BASE_BONUS;
                MobType type = target.getMobType();
                if (type == MobType.UNDEAD || type == MobType.ARTHROPOD) {
                    bonus += DAMAGE_XX_SPECIAL_BONUS;
                }
                event.setAmount(event.getAmount() + bonus * damageLevel);
            }
        }

        int protectionLevel = 0;
        for (ItemStack armor : target.getArmorSlots()) {
            protectionLevel = Math.max(protectionLevel,
                    EnchantmentHelper.getItemEnchantmentLevel(UltimateEnchanting.PROTECTION.get(), armor));
        }
        if (protectionLevel > 0) {
            // Equivalent to Protection XX: 20 protection points, capped at 80% reduction.
            event.setAmount(event.getAmount() * 0.20F);
        }
    }
}
