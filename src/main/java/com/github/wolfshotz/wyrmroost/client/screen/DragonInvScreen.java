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
    public void render(GuiGraphics ms, int mouseX, int mouseY, float partialTicks)
    {
        super.render(ms, mouseX, mouseY, partialTicks);
        renderTooltip(ms, mouseX, mouseY);
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

    @Override
    protected void renderLabels(GuiGraphics ms, int x, int y)
    {
        String name = menu.inventory.dragon.getName().getString();
        ms.drawString(font, name, (imageWidth / 2 - font.width(name) / 2), 6, 0x404040);
        ms.drawString(font, playerInventoryTitle.getString(), 8, (this.imageHeight - 96 + 2), 4210752);
    }
}
