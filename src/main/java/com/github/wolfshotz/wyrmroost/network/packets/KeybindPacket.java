package com.github.wolfshotz.wyrmroost.network.packets;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Created by com.github.WolfShotz - 8/9/2019 - 02:03
 * <p>
 * Class Handling the packet sending of keybind inputs.
 * keybinds are assigned an int, and as such follow the following format:
 */
public record KeybindPacket(byte key, int mods, boolean pressed) implements CustomPacketPayload {
    public static final Type<KeybindPacket> TYPE = new Type<>(Wyrmroost.rl("keybind"));
    public static final StreamCodec<ByteBuf, KeybindPacket> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> payload.encode(buf),
            KeybindPacket::new
    );
    public static final byte MOUNT_KEY1 = 1;
    public static final byte MOUNT_KEY2 = 2;

    public KeybindPacket(ByteBuf buf) {
        this(buf.readByte(), buf.readInt(), buf.readBoolean());
    }

    public void encode(ByteBuf buf)
    {
        buf.writeByte(key);
        buf.writeInt(mods);
        buf.writeBoolean(pressed);
    }

    public boolean handle(IPayloadContext context) { return process(context.player()); }

    public boolean process(Player player)
    {
        switch (key)
        {
            case MOUNT_KEY1:
            case MOUNT_KEY2:
                Entity vehicle = player.getVehicle();
                if (vehicle instanceof AbstractDragonEntity)
                {
                    AbstractDragonEntity dragon = ((AbstractDragonEntity) vehicle);
                    if (dragon.isTame() && dragon.getControllingPlayer() == player)
                        dragon.recievePassengerKeybind(key, mods, pressed);
                }
                break;
            default:
                Wyrmroost.LOG.warn(String.format("Recieved invalid keybind code: %s How tf did u break this", key));
                return false;
        }
        return true;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
