package com.example.ultimateenchanting.menu;

import com.example.ultimateenchanting.UltimateEnchanting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class UltimateEnchantingMenu extends AbstractContainerMenu {
    public static final int APPLY_BASE = 1_000_000;
    private static final int LEVELS_PER_ENCHANTMENT = 11;
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
        addSlot(new Slot(container, 0, 20, 45));
        addSlot(new Slot(container, 1, 20, 85) {
            @Override public boolean mayPlace(ItemStack stack) { return stack.is(Items.LAPIS_LAZULI); }
            @Override public int getMaxStackSize() { return 64; }
        });
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            addSlot(new Slot(inventory, col + row * 9 + 9, 76 + col * 18, 178 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 76 + col * 18, 236));
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

    private int maximumFor(Enchantment enchantment) {
        int configuredLimit = Math.min(10, UltimateEnchanting.LEVEL_LIMIT.get());
        if (enchantment == UltimateEnchanting.VITALITY.get()) return Math.min(5, configuredLimit);
        return configuredLimit;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id < APPLY_BASE || player.level().isClientSide) return false;
        int packed = id - APPLY_BASE;
        int enchantmentIndex = packed / LEVELS_PER_ENCHANTMENT;
        int requestedLevel = packed % LEVELS_PER_ENCHANTMENT;
        List<Enchantment> available = getAvailableEnchantments();
        if (enchantmentIndex < 0 || enchantmentIndex >= available.size() || requestedLevel <= 0) return false;

        ItemStack target = container.getItem(0);
        ItemStack lapis = container.getItem(1);
        if (target.isEmpty() || lapis.isEmpty()) return false;

        Enchantment enchantment = available.get(enchantmentIndex);
        int maximum = maximumFor(enchantment);
        if (requestedLevel > maximum) {
            player.displayClientMessage(Component.literal("El nivel máximo permitido para este encantamiento es " + maximum + "."), true);
            return false;
        }

        Map<Enchantment, Integer> currentEnchantments = EnchantmentHelper.getEnchantments(target);
        int currentLevel = currentEnchantments.getOrDefault(enchantment, 0);
        if (requestedLevel <= currentLevel) {
            player.displayClientMessage(Component.literal("El objeto ya tiene este nivel o uno superior."), true);
            return false;
        }

        int xpCost = Math.max(1, requestedLevel);
        int lapisCost = Math.max(1, (requestedLevel + 1) / 2);
        if (player.experienceLevel < xpCost) {
            player.displayClientMessage(Component.literal("Necesitas " + xpCost + " niveles de experiencia."), true);
            return false;
        }
        if (lapis.getCount() < lapisCost) {
            player.displayClientMessage(Component.literal("Necesitas " + lapisCost + " lapislázuli."), true);
            return false;
        }

        currentEnchantments.put(enchantment, requestedLevel);
        EnchantmentHelper.setEnchantments(currentEnchantments, target);
        lapis.shrink(lapisCost);
        player.giveExperienceLevels(-xpCost);
        container.setChanged();
        broadcastChanges();
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack empty = ItemStack.EMPTY;
        if (index < 0 || index >= slots.size()) return empty;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return empty;
        ItemStack source = slot.getItem();
        ItemStack original = source.copy();

        if (index < 2) {
            if (!moveItemStackTo(source, 2, 38, true)) return empty;
        } else if (source.is(Items.LAPIS_LAZULI)) {
            if (!moveItemStackTo(source, 1, 2, false)) return empty;
        } else if (container.getItem(0).isEmpty()) {
            if (!moveItemStackTo(source, 0, 1, false)) return empty;
        } else if (index < 29) {
            if (!moveItemStackTo(source, 29, 38, false)) return empty;
        } else {
            if (!moveItemStackTo(source, 2, 29, false)) return empty;
        }

        if (source.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        if (source.getCount() == original.getCount()) return empty;
        slot.onTake(player, source);
        return original;
    }

    @Override public boolean stillValid(Player player) {
        return container.stillValid(player) && access.evaluate((level, pos) -> level.getBlockState(pos).is(UltimateEnchanting.ENCHANTING_TABLE.get()), true);
    }

    @Override public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
        if (!player.level().isClientSide) clearContainer(player, container);
    }
}
