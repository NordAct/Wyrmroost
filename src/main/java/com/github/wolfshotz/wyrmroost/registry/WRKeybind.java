package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import com.github.wolfshotz.wyrmroost.network.packets.KeybindPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/**
 * @see GLFW
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber
public class WRKeybind extends KeyMapping
{
    private final byte id;
    private boolean prevIsPressed;

    public WRKeybind(String name, int keyCode, byte packetKeyID)
    {
        super(name, KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.KEYSYM.getOrCreate(keyCode), "keyCategory.wyrmroost");
        this.id = packetKeyID;
    }

    @Override
    public void setDown(boolean pressed)
    {
        super.setDown(pressed);

        if (ClientEvents.getPlayer() != null && prevIsPressed != pressed)
        {
            byte mods = 0;
            if (Screen.hasAltDown()) mods |= GLFW.GLFW_MOD_ALT;
            if (Screen.hasControlDown()) mods |= GLFW.GLFW_MOD_CONTROL;
            if (Screen.hasShiftDown()) mods |= GLFW.GLFW_MOD_SHIFT;
            KeybindPacket packet = new KeybindPacket(id, mods, pressed);
            packet.process(ClientEvents.getPlayer());
            PacketDistributor.sendToServer(packet);
        }
        prevIsPressed = pressed;
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event)
    {
        event.register(new WRKeybind("key.mountKey1", GLFW.GLFW_KEY_V, KeybindPacket.MOUNT_KEY1));
        event.register(new WRKeybind("key.mountKey2", GLFW.GLFW_KEY_G, KeybindPacket.MOUNT_KEY2));
    }
}
