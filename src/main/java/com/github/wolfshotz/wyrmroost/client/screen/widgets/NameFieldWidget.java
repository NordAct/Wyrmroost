package com.github.wolfshotz.wyrmroost.client.screen.widgets;

import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.network.packets.RenameEntityPacket;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class NameFieldWidget extends EditBox
{
    public NameFieldWidget(Font font, int posX, int posY, int sizeX, int sizeY, AbstractDragonEntity dragon)
    {
        super(font, posX, posY, sizeX, sizeY, dragon.getName());

        setValue(getMessage().getString());
        setCanLoseFocus(true);
        setBordered(false);
        setMaxLength(35);
        setResponder(s ->
        {
            if (s.equals(dragon.getName().getString())) return;
            Component name = s.isEmpty()? null : Component.literal(s);
            PacketDistributor.sendToServer(new RenameEntityPacket(dragon, name));
        });
    }
}
