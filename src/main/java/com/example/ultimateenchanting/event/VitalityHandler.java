package com.example.ultimateenchanting.event;

import com.example.ultimateenchanting.UltimateEnchanting;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class VitalityHandler {
    private static final UUID VITALITY_MODIFIER_ID = UUID.fromString("65b6b5e9-65c7-4c7f-8bde-7a7e38a10001");

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;

        Player player = event.player;
        // Armor enchantments are checked twice per second instead of on every game tick.
        if (player.tickCount % 10 != 0) return;

        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) return;

        int highestLevel = 0;
        for (ItemStack armorPiece : player.getArmorSlots()) {
            int level = EnchantmentHelper.getItemEnchantmentLevel(UltimateEnchanting.VITALITY.get(), armorPiece);
            // Vitality is hard-capped at V even if another mod or command writes an invalid level.
            level = Math.max(0, Math.min(5, level));
            highestLevel = Math.max(highestLevel, level);
        }

        double desiredBonus = highestLevel * 2.0;
        AttributeModifier current = maxHealth.getModifier(VITALITY_MODIFIER_ID);
        if (desiredBonus <= 0.0) {
            if (current != null) maxHealth.removeModifier(VITALITY_MODIFIER_ID);
            return;
        }

        if (current == null || Math.abs(current.getAmount() - desiredBonus) > 0.001) {
            maxHealth.removeModifier(VITALITY_MODIFIER_ID);
            maxHealth.addTransientModifier(new AttributeModifier(VITALITY_MODIFIER_ID,
                    "Ultimate Enchanting Vitality", desiredBonus, AttributeModifier.Operation.ADDITION));
        }
    }
}
