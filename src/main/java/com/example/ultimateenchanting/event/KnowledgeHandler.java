package com.example.ultimateenchanting.event;

import com.example.ultimateenchanting.UltimateEnchanting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KnowledgeHandler {
    // Large fixed reward per kill; the enchantment is intentionally rare and costly.
    private static final int BONUS_LEVELS_PER_KILL = 3;

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide) return;

        if (!(event.getSource().getEntity() instanceof Player player)) return;
        int level = EnchantmentHelper.getItemEnchantmentLevel(
                UltimateEnchanting.KNOWLEDGE.get(), player.getMainHandItem());
        if (level <= 0) return;

        // Award levels directly so the reward remains substantial at high XP levels.
        player.giveExperienceLevels(BONUS_LEVELS_PER_KILL * level);
    }
}
