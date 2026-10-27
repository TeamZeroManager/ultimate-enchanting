package com.example.ultimateenchanting.menu;

import com.example.ultimateenchanting.UltimateEnchanting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.EnchantedBookItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UltimateEnchantingMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerLevelAccess access;

    public UltimateEnchantingMenu(int id, Inventory inventory, FriendlyByteBuf data) {
        this(id, inventory, new SimpleContainer(2), ContainerLevelAccess.NULL);
    }

    public UltimateEnchantingMenu(int id, Inventory inventory, Container container, ContainerLevelAccess access) {
        super(UltimateEnchanting.ENCHANTING_MENU.get(), id);
        checkContainerSize(container, 2);
        this.container = container;
        this.access = access;
        container.startOpen(inventory.player);
        addSlot(new Slot(container, 0, 26, 35));
        addSlot(new Slot(container, 1, 26, 75) {
            @Override public boolean mayPlace(ItemStack stack) { return stack.is(Items.LAPIS_LAZULI); }
            @Override public int getMaxStackSize() { return 64; }
        });
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 120 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 178));
    }

    public List<Enchantment> getAvailableEnchantments() {
        ItemStack stack = container.getItem(0);
        if (stack.isEmpty()) return List.of();
        boolean book = stack.is(Items.BOOK) || stack.is(Items.ENCHANTED_BOOK);
        List<Enchantment> list = new ArrayList<>();
        BuiltInRegistries.ENCHANTMENT.forEach(enchantment -> {
            if (book || enchantment.canEnchant(stack)) list.add(enchantment);
        });
        list.sort(Comparator.comparing(e -> BuiltInRegistries.ENCHANTMENT.getKey(e).toString()));
        return list;
    }

    @Override public boolean clickMenuButton(Player player, int id) {
        List<Enchantment> available = getAvailableEnchantments();
        if (id < 0 || id >= available.size() || player.level().isClientSide) return false;
        ItemStack target = container.getItem(0);
        ItemStack lapis = container.getItem(1);
        if (target.isEmpty() || lapis.isEmpty() || player.experienceLevel < 1) return false;
        Enchantment enchantment = available.get(id);
        int maxLevel = enchantment.getMaxLevel();
        if (target.is(Items.BOOK)) {
            ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
            boolean stackedBooks = target.getCount() > 1;
            if (stackedBooks) {
                target.shrink(1);
                container.setItem(0, target);
            } else {
                container.setItem(0, enchantedBook);
            }
            EnchantedBookItem.addEnchantment(enchantedBook, new EnchantmentInstance(enchantment, maxLevel));
            if (stackedBooks && !player.getInventory().add(enchantedBook)) player.drop(enchantedBook, false);
        } else if (target.is(Items.ENCHANTED_BOOK)) {
            EnchantedBookItem.addEnchantment(target, new EnchantmentInstance(enchantment, maxLevel));
        } else {
            target.enchant(enchantment, maxLevel);
        }
        lapis.shrink(1);
        player.giveExperienceLevels(-1);
        container.setChanged();
        broadcastChanges();
        return true;
    }

    @Override public boolean stillValid(Player player) {
        return container.stillValid(player) && access.evaluate((level, pos) -> level.getBlockState(pos).is(UltimateEnchanting.ENCHANTING_TABLE.get()), true);
    }

    @Override public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
        if (!player.level().isClientSide) {
            clearContainer(player, container);
        }
    }
}
