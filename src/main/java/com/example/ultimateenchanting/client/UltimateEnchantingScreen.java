package com.example.ultimateenchanting.client;

import com.example.ultimateenchanting.UltimateEnchanting;
import com.example.ultimateenchanting.menu.UltimateEnchantingMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UltimateEnchantingScreen extends AbstractContainerScreen<UltimateEnchantingMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/enchanting_table.png");
    private static final int VISIBLE_ROWS = 5;
    private static final int ROW_HEIGHT = 31;
    private static final int LIST_X = 64;
    private static final int LIST_WIDTH = 250;
    private static final int SLIDER_X = 184;
    private static final int SLIDER_WIDTH = 70;
    private static final int APPLY_X = 266;
    private final Map<String, Integer> selectedLevels = new HashMap<>();
    private int scrollOffset;
    private int draggingIndex = -1;

    public UltimateEnchantingScreen(UltimateEnchantingMenu menu, net.minecraft.world.entity.player.Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 320;
        imageHeight = 320;
        inventoryLabelY = 200;
    }

    private String key(Enchantment enchantment) {
        return net.minecraft.core.registries.BuiltInRegistries.ENCHANTMENT.getKey(enchantment).toString();
    }

    private int maxLevel(Enchantment enchantment) {
        int cap = Math.min(10, UltimateEnchanting.LEVEL_LIMIT.get());
        if (enchantment == UltimateEnchanting.VITALITY.get()) return 5;
        return cap;
    }

    private int currentLevel(Enchantment enchantment) {
        return EnchantmentHelper.getItemEnchantmentLevel(enchantment, menu.getSlot(0).getItem());
    }

    private int selectedLevel(Enchantment enchantment) {
        String key = key(enchantment);
        int current = currentLevel(enchantment);
        int max = maxLevel(enchantment);
        return selectedLevels.computeIfAbsent(key, ignored -> current > 0 ? Math.min(current, max) : 1);
    }

    private void setSelectedLevel(Enchantment enchantment, int level) {
        selectedLevels.put(key(enchantment), Math.max(0, Math.min(maxLevel(enchantment), level)));
    }

    private int levelFromMouse(double mouseX, Enchantment enchantment) {
        double fraction = Math.max(0.0, Math.min(1.0, (mouseX - (leftPos + SLIDER_X)) / SLIDER_WIDTH));
        return (int) Math.round(fraction * maxLevel(enchantment));
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF211B29);
        graphics.fill(leftPos + 5, topPos + 5, leftPos + imageWidth - 5, topPos + 155, 0xFF34283F);
        graphics.fill(leftPos + 10, topPos + 25, leftPos + 55, topPos + 111, 0xFF211A28);
        graphics.fill(leftPos + 60, topPos + 25, leftPos + imageWidth - 8, topPos + 185, 0xFF292131);
        graphics.fill(leftPos + 5, topPos + 190, leftPos + imageWidth - 5, topPos + imageHeight - 5, 0xFF3A303F);

        ItemStack target = menu.getSlot(0).getItem();
        ItemStack lapis = menu.getSlot(1).getItem();
        graphics.renderItem(target, leftPos + 24, topPos + 42);
        graphics.renderItem(lapis, leftPos + 24, topPos + 82);

        List<Enchantment> enchants = menu.getAvailableEnchantments();
        int bottom = topPos + 25 + VISIBLE_ROWS * ROW_HEIGHT;
        graphics.enableScissor(leftPos + LIST_X, topPos + 25, leftPos + LIST_X + LIST_WIDTH, Math.min(bottom, topPos + 183));
        for (int row = 0; row < VISIBLE_ROWS; row++) {
            int index = scrollOffset + row;
            if (index >= enchants.size()) break;
            Enchantment enchantment = enchants.get(index);
            int y = topPos + 27 + row * ROW_HEIGHT;
            int selected = selectedLevel(enchantment);
            int current = currentLevel(enchantment);
            boolean hovered = mouseX >= leftPos + LIST_X && mouseX < leftPos + LIST_X + LIST_WIDTH
                    && mouseY >= y && mouseY < y + ROW_HEIGHT - 2;
            graphics.fill(leftPos + LIST_X, y, leftPos + LIST_X + LIST_WIDTH, y + ROW_HEIGHT - 2,
                    hovered ? 0xFF554465 : 0xFF40344D);

            Component name = enchantment.getFullname(Math.max(1, Math.min(selected == 0 ? 1 : selected, maxLevel(enchantment))));
            String label = name.getString();
            if (label.length() > 18) label = label.substring(0, 17) + "…";
            graphics.drawString(font, label, leftPos + LIST_X + 5, y + 3, 0xFFF7F0FF, false);
            String levelText = selected == 0 ? "—" : toRoman(selected);
            graphics.drawString(font, levelText, leftPos + LIST_X + 136, y + 3, 0xFFDCCBFA, false);

            int sliderY = y + 15;
            graphics.fill(leftPos + SLIDER_X, sliderY, leftPos + SLIDER_X + SLIDER_WIDTH, sliderY + 4, 0xFF17131C);
            int max = maxLevel(enchantment);
            int fillWidth = max == 0 ? 0 : (int) Math.round((SLIDER_WIDTH - 2) * (selected / (double) max));
            graphics.fill(leftPos + SLIDER_X + 1, sliderY + 1, leftPos + SLIDER_X + 1 + fillWidth, sliderY + 3, 0xFFB89AE8);
            int knobX = leftPos + SLIDER_X + 1 + fillWidth;
            graphics.fill(knobX - 2, sliderY - 2, knobX + 2, sliderY + 6, 0xFFE9D9FF);

            int xpCost = Math.max(1, selected);
            int lapisCost = Math.max(1, (selected + 1) / 2);
            boolean removing = selected == 0 && current > 0;
            boolean redundant = selected > 0 && selected <= current;
            boolean canRemove = removing;
            boolean canPay = !redundant && selected > current && menu.getSlot(1).getItem().getCount() >= lapisCost;
            int buttonColor = canRemove ? 0xFF8B554F : (redundant ? 0xFF514A58 : (canPay ? 0xFF685080 : 0xFF55414B));
            graphics.fill(leftPos + APPLY_X, y + 5, leftPos + APPLY_X + 39, y + 21, buttonColor);
            graphics.drawCenteredString(font, removing ? "Quitar" : (redundant ? "OK" : "Aplicar"), leftPos + APPLY_X + 19, y + 9, 0xFFFFFFFF);
            graphics.drawString(font, "XP " + xpCost + " · L " + lapisCost, leftPos + SLIDER_X, y + 22, 0xFFC9BBD8, false);
        }
        graphics.disableScissor();

        graphics.drawString(font, "Límite: " + toRoman(UltimateEnchanting.LEVEL_LIMIT.get()), leftPos + 12, topPos + 128, 0xFFE9D7FF, false);
        graphics.drawString(font, "Encantamientos: " + enchants.size(), leftPos + 12, topPos + 140, 0xFFCFC0DF, false);
        if (scrollOffset > 0) graphics.drawString(font, "▲", leftPos + 299, topPos + 14, 0xFFFFFFFF, false);
        if (scrollOffset + VISIBLE_ROWS < enchants.size()) graphics.drawString(font, "▼", leftPos + 299, topPos + 184, 0xFFFFFFFF, false);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, "Ultimate Enchanting", 10, 10, 0xFFFFFFFF, false);
        graphics.drawString(font, "Objeto", 14, 29, 0xFFDCCBEA, false);
        graphics.drawString(font, "Lapislázuli", 10, 70, 0xFFDCCBEA, false);
        graphics.drawString(font, "Inventario", 8, inventoryLabelY, 0xFFFFFFFF, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= leftPos + LIST_X && mouseX < leftPos + LIST_X + LIST_WIDTH
                && mouseY >= topPos + 27 && mouseY < topPos + 27 + VISIBLE_ROWS * ROW_HEIGHT) {
            int row = (int) ((mouseY - (topPos + 27)) / ROW_HEIGHT);
            int index = scrollOffset + row;
            List<Enchantment> enchants = menu.getAvailableEnchantments();
            if (row >= 0 && row < VISIBLE_ROWS && index < enchants.size()) {
                Enchantment enchantment = enchants.get(index);
                int relativeX = (int) mouseX - (leftPos + SLIDER_X);
                int relativeY = (int) mouseY - (topPos + 27 + row * ROW_HEIGHT);
                if (relativeY >= 12 && relativeY <= 20 && relativeX >= -2 && relativeX <= SLIDER_WIDTH + 2) {
                    draggingIndex = index;
                    setSelectedLevel(enchantment, levelFromMouse(mouseX, enchantment));
                    return true;
                }
                if (mouseX >= leftPos + APPLY_X && mouseX < leftPos + APPLY_X + 40) {
                    int level = selectedLevel(enchantment);
                    int current = currentLevel(enchantment);
                    if ((level == 0 && current > 0) || level > current) {
                        int action = UltimateEnchantingMenu.APPLY_BASE + index * 11 + level;
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action);
                        if (level == 0) selectedLevels.remove(key(enchantment));
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingIndex >= 0 && button == 0) {
            List<Enchantment> enchants = menu.getAvailableEnchantments();
            if (draggingIndex < enchants.size()) {
                setSelectedLevel(enchants.get(draggingIndex), levelFromMouse(mouseX, enchants.get(draggingIndex)));
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && draggingIndex >= 0) {
            draggingIndex = -1;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX >= leftPos + LIST_X && mouseX < leftPos + LIST_X + LIST_WIDTH
                && mouseY >= topPos + 25 && mouseY < topPos + 183) {
            int max = Math.max(0, menu.getAvailableEnchantments().size() - VISIBLE_ROWS);
            scrollOffset = Math.max(0, Math.min(max, scrollOffset - (int) Math.signum(delta)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private static String toRoman(int value) {
        if (value <= 0) return "0";
        String[] numerals = {"X", "IX", "V", "IV", "I"};
        int[] values = {10, 9, 5, 4, 1};
        StringBuilder result = new StringBuilder();
        int remaining = value;
        for (int i = 0; i < values.length; i++) {
            while (remaining >= values[i]) {
                result.append(numerals[i]);
                remaining -= values[i];
            }
        }
        return result.toString();
    }
}
