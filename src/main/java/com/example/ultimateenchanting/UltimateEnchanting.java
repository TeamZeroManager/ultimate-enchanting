package com.example.ultimateenchanting;

import com.example.ultimateenchanting.block.UltimateEnchantingTableBlock;
import com.example.ultimateenchanting.blockentity.UltimateEnchantingTableBlockEntity;
import com.example.ultimateenchanting.enchantment.VitalityEnchantment;
import com.example.ultimateenchanting.event.VitalityHandler;
import com.example.ultimateenchanting.event.MatadiosesHandler;
import com.example.ultimateenchanting.item.MatadiosesSwordItem;
import com.example.ultimateenchanting.item.AncientWordLensItem;
import com.example.ultimateenchanting.event.RelicLootHandler;
import com.example.ultimateenchanting.menu.UltimateEnchantingMenu;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import java.util.List;
import net.minecraftforge.common.extensions.IForgeMenuType;

@Mod(UltimateEnchanting.MODID)
public class UltimateEnchanting {
    public static final String MODID = "ultimate_enchanting";
    private static final ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.IntValue LEVEL_LIMIT = CONFIG_BUILDER
            .comment("Maximum level allowed by the Ultimate Enchanting Table (1-10).")
            .defineInRange("enchantmentLevelLimit", 10, 1, 10);
    public static final ForgeConfigSpec SERVER_CONFIG = CONFIG_BUILDER.build();
    private static ModConfig serverConfig;

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);

    public static final RegistryObject<Block> ENCHANTING_TABLE = BLOCKS.register("ultimate_enchanting_table",
            () -> new UltimateEnchantingTableBlock(BlockBehaviour.Properties.copy(Blocks.ENCHANTING_TABLE).noOcclusion()));
    public static final RegistryObject<Item> ENCHANTING_TABLE_ITEM = ITEMS.register("ultimate_enchanting_table",
            () -> new BlockItem(ENCHANTING_TABLE.get(), new Item.Properties()));

    public static final RegistryObject<Item> VERBO_LENS = ITEMS.register("lente_verbo_antiguo",
            () -> new AncientWordLensItem(new Item.Properties().stacksTo(1).fireResistant()));

    public static final RegistryObject<Item> MATADIOSES_7 = ITEMS.register("matadioses_7",
            () -> new MatadiosesSwordItem(10, "item.ultimate_enchanting.matadioses_7.lore", new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> MATADIOSES_10 = ITEMS.register("matadioses_10",
            () -> new MatadiosesSwordItem(12, "item.ultimate_enchanting.matadioses_10.lore", new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> MATADIOSES_13 = ITEMS.register("matadioses_13",
            () -> new MatadiosesSwordItem(15, "item.ultimate_enchanting.matadioses_13.lore", new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> MATADIOSES_16 = ITEMS.register("matadioses_16",
            () -> new MatadiosesSwordItem(17, "item.ultimate_enchanting.matadioses_16.lore", new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> MATADIOSES_20 = ITEMS.register("matadioses_20",
            () -> new MatadiosesSwordItem(20, "item.ultimate_enchanting.matadioses_20.lore", new Item.Properties().fireResistant()));
    public static final List<RegistryObject<Item>> MATADIOSES_ITEMS = List.of(
            MATADIOSES_7, MATADIOSES_10, MATADIOSES_13, MATADIOSES_16, MATADIOSES_20);

    public static final RegistryObject<BlockEntityType<UltimateEnchantingTableBlockEntity>> ENCHANTING_TABLE_ENTITY = BLOCK_ENTITIES.register(
            "ultimate_enchanting_table", () -> BlockEntityType.Builder.of(UltimateEnchantingTableBlockEntity::new, ENCHANTING_TABLE.get()).build(null));
    public static final RegistryObject<MenuType<UltimateEnchantingMenu>> ENCHANTING_MENU = MENUS.register("ultimate_enchanting",
            () -> IForgeMenuType.create(UltimateEnchantingMenu::new));
    public static final RegistryObject<Enchantment> VITALITY = ENCHANTMENTS.register("vitality", VitalityEnchantment::new);

    public UltimateEnchanting() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        MENUS.register(bus);
        ENCHANTMENTS.register(bus);
        bus.addListener(this::onConfigLoading);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        MinecraftForge.EVENT_BUS.register(new VitalityHandler());
        MinecraftForge.EVENT_BUS.register(new MatadiosesHandler());
        MinecraftForge.EVENT_BUS.register(new RelicLootHandler());
    }

    private void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("ultimateenchanting")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("limit")
                        .executes(context -> {
                            context.getSource().sendSuccess(() -> Component.literal("Límite actual de Ultimate Enchanting: " + LEVEL_LIMIT.get() + " (máximo X)."), false);
                            return 1;
                        })
                        .then(Commands.argument("value", IntegerArgumentType.integer(1))
                                .executes(context -> {
                                    int requested = IntegerArgumentType.getInteger(context, "value");
                                    if (requested > 10) {
                                        context.getSource().sendFailure(Component.literal("El límite máximo es X (10)."));
                                        return 0;
                                    }
                                    LEVEL_LIMIT.set(requested);
                                    saveServerConfig();
                                    context.getSource().sendSuccess(() -> Component.literal("Límite de encantamiento establecido en " + requested + "."), true);
                                    return 1;
                                })))
                .then(Commands.literal("reset")
                        .executes(context -> {
                            LEVEL_LIMIT.set(10);
                            saveServerConfig();
                            context.getSource().sendSuccess(() -> Component.literal("Límite restaurado a X (10)."), true);
                            return 1;
                        })));
    }

    private void onConfigLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SERVER_CONFIG) serverConfig = event.getConfig();
    }

    private void saveServerConfig() {
        if (serverConfig != null) serverConfig.save();
    }
}
