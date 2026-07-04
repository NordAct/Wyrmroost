package com.github.wolfshotz.wyrmroost.client.screen.widgets;

import com.github.wolfshotz.wyrmroost.items.staff.DragonStaffItem;
import com.github.wolfshotz.wyrmroost.items.staff.StaffAction;
import com.github.wolfshotz.wyrmroost.network.packets.StaffActionPacket;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import com.github.wolfshotz.wyrmroost.util.TickFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class StaffActionButton extends AbstractButton
{
    public final StaffAction action;
    public final TickFloat focusTime = new TickFloat().setLimit(0, 1);
    public boolean wasHovered = false;

    public StaffActionButton(int xIn, int yIn, Component msg, StaffAction action)
    {
        super(xIn, yIn, 100, 20, msg);
        this.action = action;
    }

    @Override
    public void onPress()
    {
        Player player = Minecraft.getInstance().player;
        ItemStack stack = ModUtils.getHeldStack(player, WRItems.DRAGON_STAFF.value());
        if (stack.getItem() == WRItems.DRAGON_STAFF.value())
        {
            DragonStaffItem.setAction(action, player, stack);
            PacketDistributor.sendToServer(new StaffActionPacket(action));
        }
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public void renderWidget(GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        if (wasHovered != isHovered) setFocused(wasHovered = isHovered);

        float time = 0.5f * partialTicks; // adjust speed for framerate
        focusTime.add(isHovered? time : -time);
        float amount = focusTime.get(partialTicks) * 6;
        ms.drawCenteredString(
                Minecraft.getInstance().font,
                getMessage(),
                getX() + width / 2,
                (getY() + (height - 8) / 2) - (int) amount,
                Mth.lerpInt(amount, 0xffffff, 0xfffd8a));
    }

    @Override
    public void setFocused(boolean focusing)
    {
        if (focusing)
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_BASS, -1f));
        super.setFocused(focusing);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
