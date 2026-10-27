package com.example.ultimateenchanting.client;

import com.example.ultimateenchanting.menu.UltimateEnchantingMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.resources.ResourceLocation;
import java.util.List;

public class UltimateEnchantingScreen extends AbstractContainerScreen<UltimateEnchantingMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/enchanting_table.png");
    private int scrollOffset = 0;
    private static final int ROWS = 5;

    public UltimateEnchantingScreen(UltimateEnchantingMenu menu, net.minecraft.world.entity.player.Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 220;
        inventoryLabelY = 106;
    }

    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF29202F);
        graphics.fill(leftPos + 5, topPos + 5, leftPos + imageWidth - 5, topPos + 101, 0xFF45364E);
        graphics.fill(leftPos + 68, topPos + 26, leftPos + 170, topPos + 101, 0xFF211A26);
        graphics.fill(leftPos + 5, topPos + 108, leftPos + imageWidth - 5, topPos + imageHeight - 5, 0xFF3A303F);
        graphics.renderItem(menu.getSlot(0).getItem(), leftPos + 28, topPos + 35);
        graphics.renderItem(menu.getSlot(1).getItem(), leftPos + 28, topPos + 75);
        List<Enchantment> enchants = menu.getAvailableEnchantments();
        for (int row = 0; row < ROWS; row++) {
            int index = scrollOffset + row;
            if (index >= enchants.size()) break;
            Enchantment enchantment = enchants.get(index);
            int y = topPos + 28 + row * 14;
            boolean hovered = mouseX >= leftPos + 69 && mouseX < leftPos + 169 && mouseY >= y && mouseY < y + 13;
            graphics.fill(leftPos + 69, y, leftPos + 169, y + 13, hovered ? 0xFF76628B : 0xFF55465F);
            Component name = enchantment.getFullname(enchantment.getMaxLevel());
            String text = name.getString();
            if (text.length() > 17) text = text.substring(0, 16) + "…";
            graphics.drawString(font, text, leftPos + 72, y + 3, 0xFFFFFFFF, false);
        }
        graphics.drawString(font, "Encantamientos: " + enchants.size(), leftPos + 8, topPos + 91, 0xFFE9D7FF, false);
        if (scrollOffset > 0) graphics.drawString(font, "▲", leftPos + 158, topPos + 14, 0xFFFFFFFF, false);
        if (scrollOffset + ROWS < enchants.size()) graphics.drawString(font, "▼", leftPos + 158, topPos + 100, 0xFFFFFFFF, false);
    }

    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 8, 0xFFFFFFFF, false);
        graphics.drawString(font, "Objeto", 8, 22, 0xFFDCCBEA, false);
        graphics.drawString(font, "Lapislázuli", 8, 62, 0xFFDCCBEA, false);
        graphics.drawString(font, "1 nivel + 1 lapislázuli por encantamiento", 8, 96, 0xFFDCCBEA, false);
        graphics.drawString(font, "Inventario", 8, inventoryLabelY, 0xFFFFFFFF, false);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= leftPos + 69 && mouseX < leftPos + 170 && mouseY >= topPos + 28 && mouseY < topPos + 98) {
            int row = (int) ((mouseY - (topPos + 28)) / 14);
            int index = scrollOffset + row;
            if (row >= 0 && row < ROWS && index < menu.getAvailableEnchantments().size()) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, index);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX >= leftPos + 69 && mouseX < leftPos + 170 && mouseY >= topPos + 26 && mouseY < topPos + 101) {
            int max = Math.max(0, menu.getAvailableEnchantments().size() - ROWS);
            scrollOffset = Math.max(0, Math.min(max, scrollOffset - (int) Math.signum(delta)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
