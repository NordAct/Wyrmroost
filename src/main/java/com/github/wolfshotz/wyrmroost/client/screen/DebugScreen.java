package com.github.wolfshotz.wyrmroost.client.screen;

import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.util.animation.Animation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DebugScreen extends Screen
{
    public final AbstractDragonEntity dragon;
    
    public DebugScreen(AbstractDragonEntity dragon)
    {
        super(Component.literal("debug_screen"));
        
        this.dragon = dragon;
    }
    
    @Override
    protected void init()
    {
        Animation[] animations = dragon.getAnimations();
        if (animations != null && animations.length > 0)
            for (int i = 0; i < animations.length; i++)
            {
                Animation animation = animations[i];
                addWidget(Button.builder(
                        Component.literal("Anim: " + i),
                        b -> {
                            dragon.setAnimation(animation);
                            onClose();
                        })
                        .bounds((i * 50) + (width / 2) - (animations.length * 25), 200, 50, 12)
                        .build());
            }
    }

    @Override
    public void render(GuiGraphics ms, int mouseX, int mouseY, float partialTicks)
    {super.render(ms, mouseX, mouseY, partialTicks);
        String gender = dragon.isMale()? "male" : "female";

        ms.drawCenteredString(font, dragon.getDisplayName().getString(), (width / 2), 15, 0xffffff);
        ms.drawCenteredString(font, "isSleeping: " + dragon.isSleeping(), (width / 2) + 50, 50, 0xffffff);
        ms.drawCenteredString(font, "isTamed: " + dragon.isTame(), (width / 2) - 50, 50, 0xffffff);
        ms.drawCenteredString(font, "isSitting: " + dragon.isInSittingPose(), (width / 2) - 50, 75, 0xffffff);
        ms.drawCenteredString(font, "isFlying: " + dragon.isFlying(), (width / 2) + 50, 75, 0xffffff);
        ms.drawCenteredString(font, "variant: " + dragon.getVariant(), (width / 2) - 50, 100, 0xffffff);
        ms.drawCenteredString(font, "gender: " + gender, (width / 2) + 50, 100, 0xffffff);
        ms.drawCenteredString(font, "health: " + dragon.getHealth() + " / " + dragon.getMaxHealth(), (width / 2) - 50, 125, 0xffffff);
        ms.drawCenteredString(font, "noAI: " + dragon.isNoAi(), (width / 2) + 50, 125, 0xffffff);
        ms.drawCenteredString(font, "position: " + dragon.position(), (width / 2), 150, 0xffffff);
        ms.drawCenteredString(font, "motion: " + dragon.getDeltaMovement(), (width / 2), 175, 0xffffff);
    }

    @Override
    public boolean isPauseScreen()
    {
        return true;
    }

    public static void open(AbstractDragonEntity dragon)
    {
        Minecraft.getInstance().setScreen(new DebugScreen(dragon));
    }
}
