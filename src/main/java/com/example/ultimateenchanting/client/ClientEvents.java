package com.example.ultimateenchanting.client;

import com.example.ultimateenchanting.UltimateEnchanting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
    modid = UltimateEnchanting.MODID,
    bus = Mod.EventBusSubscriber.Bus.MOD,
    value = Dist.CLIENT
)
public final class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() ->
            MenuScreens.register(
                UltimateEnchanting.ENCHANTING_MENU.get(),
                UltimateEnchantingScreen::new
            )
        );
    }
}
