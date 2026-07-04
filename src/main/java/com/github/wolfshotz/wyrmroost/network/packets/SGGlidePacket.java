package com.github.wolfshotz.wyrmroost.network.packets;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.SilverGliderEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SGGlidePacket(boolean gliding) implements CustomPacketPayload {
    public static final Type<SGGlidePacket> TYPE = new Type<>(Wyrmroost.rl("sg_glide"));
    public static final StreamCodec<ByteBuf, SGGlidePacket> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> payload.encode(buf),
            SGGlidePacket::new
    );

    public SGGlidePacket(ByteBuf buffer) {
        this(buffer.readBoolean());
    }

    public void encode(ByteBuf buf) {
        buf.writeBoolean(gliding);
    }

    public boolean handle(IPayloadContext context)
    {
        Player reciever = context.player();
        if (reciever != null && !reciever.getPassengers().isEmpty())
        {
            Entity entity = reciever.getFirstPassenger();
            if (entity instanceof SilverGliderEntity)
            {
                ((SilverGliderEntity) entity).isGliding = gliding;
                return true;
            }
        }
        return false;
    }

    public static void send(boolean gliding) {
        PacketDistributor.sendToServer(new SGGlidePacket(gliding));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
