package com.github.wolfshotz.wyrmroost.client.screen;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.containers.DragonInvContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class DragonInvScreen extends AbstractContainerScreen<DragonInvContainer>
{
    public static final ResourceLocation TEXTURE = Wyrmroost.rl("textures/io/dragon_inv_screen.png");

    public DragonInvScreen(DragonInvContainer screenContainer, Inventory inv, Component titleIn)
    {
        super(screenContainer, inv, titleIn);
        imageWidth = 194;
        imageHeight = 220;
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth / 2 - font.width(title) / 2);
        titleLabelY = 6;
        inventoryLabelX = 8;
        inventoryLabelY = (this.imageHeight - 96 + 2);
    }

    @Override
    protected void renderBg(GuiGraphics ms, float partialTicks, int x, int y)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int midX = (width - imageWidth) / 2;
        int midY = (height - imageHeight) / 2;
        ms.blit(TEXTURE, midX, midY, 0, 0, imageWidth, imageHeight);

        for (Slot slot : menu.slots)
            if (slot.isActive())
                ms.blit(TEXTURE, (midX + slot.x) - 1, (midY + slot.y) - 1, 194, 0, 18, 18);
    }
}
