package com.example.ultimateenchanting.event;

import com.example.ultimateenchanting.UltimateEnchanting;
import com.example.ultimateenchanting.item.MatadiosesSwordItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Set;

public class RelicLootHandler {
    private static final String LENS_DISCOVERED_TAG = "ultimate_enchanting_inscriptions_deciphered";
    private static final ResourceLocation ANCIENT_CITY_CHEST = new ResourceLocation("minecraft", "chests/ancient_city");

    private static final Set<ResourceLocation> LENS_CHESTS = Set.of(
            ANCIENT_CITY_CHEST,
            new ResourceLocation("minecraft", "chests/stronghold_library"),
            new ResourceLocation("minecraft", "chests/woodland_mansion"),
            new ResourceLocation("minecraft", "chests/jungle_temple"),
            new ResourceLocation("minecraft", "chests/desert_pyramid")
    );

    private static final Set<ResourceLocation> TABLE_CHESTS = Set.of(
            ANCIENT_CITY_CHEST,
            new ResourceLocation("minecraft", "chests/woodland_mansion"),
            new ResourceLocation("minecraft", "chests/end_city_treasure"),
            new ResourceLocation("minecraft", "chests/bastion_treasure"),
            new ResourceLocation("minecraft", "chests/stronghold_library")
    );

    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation chest = event.getName();

        if (ANCIENT_CITY_CHEST.equals(chest)) {
            // The first Matadioses (10 damage) is the introductory discovery: 10% per chest.
            event.getTable().addPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(LootItem.lootTableItem(UltimateEnchanting.MATADIOSES_7.get()))
                    .when(LootItemRandomChanceCondition.randomChance(0.10F))
                    .build());

            // The four stronger variants remain exceptionally rare: 0.4% each per chest.
            event.getTable().addPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(LootItem.lootTableItem(UltimateEnchanting.MATADIOSES_13.get()).setWeight(1))
                    .add(LootItem.lootTableItem(UltimateEnchanting.MATADIOSES_16.get()).setWeight(1))
                    .add(LootItem.lootTableItem(UltimateEnchanting.MATADIOSES_20.get()).setWeight(1))
                    .add(LootItem.lootTableItem(UltimateEnchanting.MATADIOSES_10.get()).setWeight(1))
                    .when(LootItemRandomChanceCondition.randomChance(0.016F))
                    .build());
        }

        if (LENS_CHESTS.contains(chest)) {
            event.getTable().addPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(LootItem.lootTableItem(UltimateEnchanting.VERBO_LENS.get()))
                    .when(LootItemRandomChanceCondition.randomChance(0.06F))
                    .build());
        }

        if (TABLE_CHESTS.contains(chest)) {
            event.getTable().addPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(LootItem.lootTableItem(UltimateEnchanting.ENCHANTING_TABLE_ITEM.get()))
                    .when(LootItemRandomChanceCondition.randomChance(0.035F))
                    .build());
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }

        // Inventory discovery and name updates do not need to run 20 times per second.
        if (player.tickCount % 20 != 0) {
            return;
        }

        boolean alreadyDiscovered = player.getPersistentData().getBoolean(LENS_DISCOVERED_TAG);
        if (!alreadyDiscovered) {
            boolean hasLens = player.getInventory().items.stream()
                    .anyMatch(stack -> stack.is(UltimateEnchanting.VERBO_LENS.get()));
            if (!hasLens) {
                return;
            }

            player.getPersistentData().putBoolean(LENS_DISCOVERED_TAG, true);
        }

        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof MatadiosesSwordItem && !stack.hasCustomHoverName()) {
                stack.setHoverName(Component.translatable(stack.getItem().getDescriptionId()));
            }
        }
    }
}
