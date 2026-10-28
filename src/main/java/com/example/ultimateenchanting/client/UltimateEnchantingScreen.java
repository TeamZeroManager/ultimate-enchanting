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
        // Visual-only pass: the enchantment and compatibility logic lives in the menu and is unchanged.
        final int frame = 0xFF17131D;
        final int bronze = 0xFF786044;
        final int bronzeLight = 0xFFB18A59;
        final int stone = 0xFF29232F;
        final int panel = 0xFF211C28;
        final int panelInset = 0xFF302738;
        final int cyan = 0xFF70C9D0;

        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, frame);
        // Outer frame and restrained bronze edging.
        graphics.fill(leftPos + 1, topPos + 1, leftPos + imageWidth - 1, topPos + imageHeight - 1, bronze);
        graphics.fill(leftPos + 3, topPos + 3, leftPos + imageWidth - 3, topPos + imageHeight - 3, stone);
        graphics.fill(leftPos + 5, topPos + 5, leftPos + imageWidth - 5, topPos + 188, panel);
        graphics.fill(leftPos + 5, topPos + 190, leftPos + imageWidth - 5, topPos + imageHeight - 5, panel);
        graphics.fill(leftPos + 7, topPos + 7, leftPos + imageWidth - 7, topPos + 23, 0xFF17131D);
        graphics.fill(leftPos + 10, topPos + 25, leftPos + 58, topPos + 185, panelInset);
        graphics.fill(leftPos + 61, topPos + 25, leftPos + imageWidth - 8, topPos + 185, 0xFF19151F);
        graphics.fill(leftPos + 8, topPos + 192, leftPos + imageWidth - 8, topPos + imageHeight - 8, 0xFF27212D);

        // Minecraft-style inventory slots: recessed square, dark inner fill and subtle edge.
        // Slot coordinates must match the menu's actual Slot positions.
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawInventorySlot(graphics, leftPos + 76 + col * 18, topPos + 218 + row * 18);
            }
        }
        for (int col = 0; col < 9; col++) {
            drawInventorySlot(graphics, leftPos + 76 + col * 18, topPos + 276);
        }

        // Fine double-line borders and small cyan rune-like accents.
        graphics.fill(leftPos + 8, topPos + 24, leftPos + imageWidth - 8, topPos + 25, bronze);
        graphics.fill(leftPos + 59, topPos + 25, leftPos + 60, topPos + 185, bronze);
        graphics.fill(leftPos + 8, topPos + 188, leftPos + imageWidth - 8, topPos + 189, bronze);
        graphics.fill(leftPos + 10, topPos + 8, leftPos + 26, topPos + 9, bronzeLight);
        graphics.fill(leftPos + imageWidth - 26, topPos + 8, leftPos + imageWidth - 10, topPos + 9, bronzeLight);
        graphics.fill(leftPos + 12, topPos + 11, leftPos + 14, topPos + 17, cyan);
        graphics.fill(leftPos + imageWidth - 14, topPos + 11, leftPos + imageWidth - 12, topPos + 17, cyan);

        // The menu already renders the real equipment and lapis slots. Do not paint or draw them here:
        // doing so stacks a second frame and duplicates the item icons.
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
                    hovered ? 0xFF514353 : 0xFF302735);
            graphics.fill(leftPos + LIST_X, y, leftPos + LIST_X + 2, y + ROW_HEIGHT - 2, hovered ? cyan : bronze);
            if (current > 0) graphics.fill(leftPos + LIST_X + 4, y + 3, leftPos + LIST_X + 5, y + 10, 0xFF78B88A);

            Component name = enchantment.getFullname(Math.max(1, Math.min(selected == 0 ? 1 : selected, maxLevel(enchantment))));
            String label = name.getString();
            int nameX = leftPos + LIST_X + 7;
            int nameY = y + 3;
            int nameWidth = 124;
            int textWidth = font.width(label);
            graphics.enableScissor(nameX, y + 1, nameX + nameWidth, y + 14);
            if (textWidth <= nameWidth) {
                graphics.drawString(font, label, nameX, nameY, 0xFFF4EAF8, false);
            } else {
                // Marquee: pause at each end, then glide so long localized/modded names remain readable.
                long phase = (System.currentTimeMillis() / 35L) % (long) (textWidth - nameWidth + 48);
                int offset = phase < 24 ? 0
                        : (int) Math.min(textWidth - nameWidth, phase - 24);
                graphics.drawString(font, label, nameX - offset, nameY, 0xFFF4EAF8, false);
            }
            graphics.disableScissor();
            String levelText = selected == 0 ? "—" : toRoman(selected);
            graphics.drawString(font, levelText, leftPos + LIST_X + 136, y + 3, selected == 0 ? 0xFFB68D86 : 0xFFE5D0FF, false);

            int sliderY = y + 15;
            graphics.fill(leftPos + SLIDER_X, sliderY, leftPos + SLIDER_X + SLIDER_WIDTH, sliderY + 4, 0xFF100D14);
            int max = maxLevel(enchantment);
            int fillWidth = max == 0 ? 0 : (int) Math.round((SLIDER_WIDTH - 2) * (selected / (double) max));
            if (fillWidth > 0) graphics.fill(leftPos + SLIDER_X + 1, sliderY + 1, leftPos + SLIDER_X + 1 + fillWidth, sliderY + 3, selected == 0 ? 0xFF98645F : 0xFF64BFC7);
            int knobX = leftPos + SLIDER_X + 1 + fillWidth;
            graphics.fill(knobX - 2, sliderY - 2, knobX + 2, sliderY + 6, 0xFFD8C7A5);

            int xpCost = Math.max(1, selected);
            int lapisCost = Math.max(1, (selected + 1) / 2);
            boolean removing = selected == 0 && current > 0;
            boolean redundant = selected > 0 && selected <= current;
            boolean canPay = !redundant && selected > current && menu.getSlot(1).getItem().getCount() >= lapisCost;
            int buttonColor = removing ? 0xFF754842 : (redundant ? 0xFF45404B : (canPay ? 0xFF496A69 : 0xFF51414C));
            graphics.fill(leftPos + APPLY_X, y + 5, leftPos + APPLY_X + 39, y + 21, 0xFF151119);
            graphics.fill(leftPos + APPLY_X + 1, y + 6, leftPos + APPLY_X + 38, y + 20, buttonColor);
            graphics.drawCenteredString(font, removing ? "Quitar" : (redundant ? "Actual" : "Aplicar"), leftPos + APPLY_X + 19, y + 9, 0xFFFFFFFF);
            graphics.drawString(font, "XP " + xpCost + " · L " + lapisCost, leftPos + SLIDER_X, y + 22, 0xFFC7B8D0, false);
        }
        graphics.disableScissor();

        graphics.drawString(font, "Límite " + toRoman(UltimateEnchanting.LEVEL_LIMIT.get()), leftPos + 13, topPos + 132, 0xFFE4D3F3, false);
        graphics.drawString(font, "Lista " + enchants.size(), leftPos + 13, topPos + 145, 0xFFBEB0C9, false);
        graphics.drawString(font, "MESA ARCANA", leftPos + 12, topPos + 12, 0xFFE7D7C0, false);
        if (scrollOffset > 0) graphics.drawString(font, "▲", leftPos + 299, topPos + 14, cyan, false);
        if (scrollOffset + VISIBLE_ROWS < enchants.size()) graphics.drawString(font, "▼", leftPos + 299, topPos + 181, cyan, false);
    }

    private void drawInventorySlot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x - 1, y - 1, x + 17, y + 17, 0xFF100D14);
        graphics.fill(x, y, x + 16, y + 16, 0xFF514653);
        graphics.fill(x + 1, y + 1, x + 15, y + 15, 0xFF28222E);
        graphics.fill(x + 1, y + 1, x + 15, y + 2, 0xFF17131D);
        graphics.fill(x + 1, y + 1, x + 2, y + 15, 0xFF17131D);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, "Objeto", 14, 29, 0xFFE3D0AD, false);
        graphics.drawString(font, "Lapislázuli", 10, 70, 0xFFE3D0AD, false);
        graphics.drawString(font, "Inventario", 8, inventoryLabelY, 0xFFE3D0AD, false);
        graphics.fill(8, inventoryLabelY - 4, 312, inventoryLabelY - 3, 0xFF786044);
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
