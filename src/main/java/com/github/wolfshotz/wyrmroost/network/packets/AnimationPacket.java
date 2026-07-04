package com.github.wolfshotz.wyrmroost.network.packets;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import com.github.wolfshotz.wyrmroost.util.animation.Animation;
import com.github.wolfshotz.wyrmroost.util.animation.IAnimatable;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.commons.lang3.ArrayUtils;

public record AnimationPacket(int entityID, int animationIndex) implements CustomPacketPayload {
    public static final Type<AnimationPacket> TYPE = new Type<>(Wyrmroost.rl("animation"));
    public static final StreamCodec<ByteBuf, AnimationPacket> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> payload.encode(buf),
            AnimationPacket::new
    );

    public AnimationPacket(ByteBuf buf) {
        this(buf.readInt(), buf.readInt());
    }
    
    public void encode(ByteBuf buf)
    {
        buf.writeInt(entityID);
        buf.writeInt(animationIndex);
    }

    public boolean handle(IPayloadContext context) {
        return FMLLoader.getDist() == Dist.CLIENT && ClientEvents.handleAnimationPacket(entityID, animationIndex);
    }

    public static <T extends Entity & IAnimatable> void send(T entity, Animation animation) {
        if (!entity.level().isClientSide()) {
            entity.setAnimation(animation);
            PacketDistributor.sendToAllPlayers(new AnimationPacket(entity.getId(), ArrayUtils.indexOf(entity.getAnimations(), animation)));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
