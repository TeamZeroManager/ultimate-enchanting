package com.example.ultimateenchanting;

import com.example.ultimateenchanting.block.UltimateEnchantingTableBlock;
import com.example.ultimateenchanting.blockentity.UltimateEnchantingTableBlockEntity;
import com.example.ultimateenchanting.menu.UltimateEnchantingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(UltimateEnchanting.MODID)
public class UltimateEnchanting {
    public static final String MODID = "ultimate_enchanting";
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);

    public static final RegistryObject<Block> ENCHANTING_TABLE = BLOCKS.register("ultimate_enchanting_table",
            () -> new UltimateEnchantingTableBlock(BlockBehaviour.Properties.copy(Blocks.ENCHANTING_TABLE).noOcclusion()));
    public static final RegistryObject<Item> ENCHANTING_TABLE_ITEM = ITEMS.register("ultimate_enchanting_table",
            () -> new BlockItem(ENCHANTING_TABLE.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<UltimateEnchantingTableBlockEntity>> ENCHANTING_TABLE_ENTITY = BLOCK_ENTITIES.register(
            "ultimate_enchanting_table", () -> BlockEntityType.Builder.of(UltimateEnchantingTableBlockEntity::new, ENCHANTING_TABLE.get()).build(null));
    public static final RegistryObject<MenuType<UltimateEnchantingMenu>> ENCHANTING_MENU = MENUS.register("ultimate_enchanting",
            () -> IForgeMenuType.create(UltimateEnchantingMenu::new));

    public UltimateEnchanting() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        MENUS.register(bus);
    }
}
