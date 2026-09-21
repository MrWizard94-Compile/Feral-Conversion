package com.mrwizard.generatedferallanterns.client;

import com.mrwizard.generatedferallanterns.menu.MigrationMenu;
import com.mrwizard.generatedferallanterns.network.MigrationChoicePayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public final class MigrationPromptScreen extends AbstractContainerScreen<MigrationMenu> {
    private boolean submitted;

    public MigrationPromptScreen(MigrationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        imageWidth = Math.min(440, Math.max(280, width - 30));
        imageHeight = Math.min(280, Math.max(220, height - 30));
        super.init();

        int gap = 10;
        int buttonWidth = Math.min(195, (imageWidth - 30 - gap) / 2);
        int totalWidth = buttonWidth * 2 + gap;
        int left = (width - totalWidth) / 2;
        int buttonY = topPos + imageHeight - 34;

        addRenderableWidget(Button.builder(
                Component.translatable("screen.generatedferallanterns.migration.convert"),
                button -> submit(true)
        ).bounds(left, buttonY, buttonWidth, 20).build());

        addRenderableWidget(Button.builder(
                Component.translatable("screen.generatedferallanterns.migration.new_only"),
                button -> submit(false)
        ).bounds(left + buttonWidth + gap, buttonY, buttonWidth, 20).build());
    }

    private void submit(boolean convertExisting) {
        if (submitted) {
            return;
        }
        submitted = true;
        PacketDistributor.sendToServer(new MigrationChoicePayload(convertExisting));
        super.onClose();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void onClose() {
        if (submitted) {
            super.onClose();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int right = leftPos + imageWidth;
        int bottom = topPos + imageHeight;
        graphics.fill(leftPos, topPos, right, bottom, 0xE0101010);
        graphics.fill(leftPos, topPos, right, topPos + 1, 0xFF808080);
        graphics.fill(leftPos, bottom - 1, right, bottom, 0xFF505050);
        graphics.fill(leftPos, topPos, leftPos + 1, bottom, 0xFF808080);
        graphics.fill(right - 1, topPos, right, bottom, 0xFF505050);

        int centerX = width / 2;
        int textWidth = imageWidth - 30;
        int y = topPos + 18;

        graphics.drawCenteredString(font, Component.translatable("screen.generatedferallanterns.migration.heading"), centerX, y, 0xFFFFFF);
        y += 24;

        y = drawWrapped(graphics, Component.translatable("screen.generatedferallanterns.migration.body"), centerX, y, textWidth, 0xD0D0D0);
        y += 10;
        y = drawWrapped(graphics, Component.translatable("screen.generatedferallanterns.migration.warning"), centerX, y, textWidth, 0xFFCC55);
        y += 10;
        drawWrapped(graphics, Component.translatable("screen.generatedferallanterns.migration.backup"), centerX, y, textWidth, 0xFF7777);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }

    private int drawWrapped(GuiGraphics graphics, Component text, int centerX, int y, int maxWidth, int color) {
        List<FormattedCharSequence> lines = font.split(text, maxWidth);
        for (FormattedCharSequence line : lines) {
            graphics.drawCenteredString(font, line, centerX, y, color);
            y += font.lineHeight + 2;
        }
        return y;
    }
}
