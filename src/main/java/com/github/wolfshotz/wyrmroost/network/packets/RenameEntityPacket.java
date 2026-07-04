package com.github.wolfshotz.wyrmroost.network.packets;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record RenameEntityPacket(UUID entity, Component text) implements CustomPacketPayload {
    public static final Type<RenameEntityPacket> TYPE = new Type<>(Wyrmroost.rl("rename"));
    public static final StreamCodec<FriendlyByteBuf, RenameEntityPacket> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> payload.encode(buf),
            RenameEntityPacket::new
    );

    public RenameEntityPacket(Entity entity, Component text) {
        this(entity.getUUID(), text);
    }

    public RenameEntityPacket(FriendlyByteBuf buf) {
        this(buf.readUUID(), ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.decode(buf));
    }
    
    public void encode(FriendlyByteBuf buf)
    {
        buf.writeUUID(entity);
        ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.encode(buf, text);
    }

    public boolean handle(IPayloadContext context)
    {
        ((ServerLevel)context.player().level()).getEntity(entity).setCustomName(text);
        return true;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
