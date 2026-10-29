package com.example.ultimateenchanting;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = UltimateEnchanting.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CreativeTabEvents {
    private CreativeTabEvents() {}
    @SubscribeEvent
    public static void addToFunctionalTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(UltimateEnchanting.ENCHANTING_TABLE_ITEM.get());
        }
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            UltimateEnchanting.MATADIOSES_ITEMS.forEach(item -> event.accept(item.get()));
        }
    }
}
