package com.github.wolfshotz.wyrmroost.network.packets;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AddPassengerPacket(int passengerID, int vehicleID) implements CustomPacketPayload {
    public static final Type<AddPassengerPacket> TYPE = new Type<>(Wyrmroost.rl("add_passenger"));
    public static final StreamCodec<ByteBuf, AddPassengerPacket> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> payload.encode(buf),
            AddPassengerPacket::new
    );
    AddPassengerPacket(Entity passenger, Entity vehicle) {
        this(passenger.getId(), vehicle.getId());
    }

    public AddPassengerPacket(ByteBuf buf) {
        this(buf.readInt(), buf.readInt());
    }

    public void encode(ByteBuf buf) {
        buf.writeInt(passengerID);
        buf.writeInt(vehicleID);
    }

    public boolean handle(IPayloadContext ctx) {
        if (FMLLoader.getDist() == Dist.CLIENT) handleClient();
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleClient()
    {
        Level world = ClientEvents.getLevel();
        Entity passenger = world.getEntity(passengerID);
        Entity vehicle = world.getEntity(vehicleID);
        if (passenger == null || vehicle == null || !passenger.startRiding(vehicle, true))
        {
            Wyrmroost.LOG.warn("Could not add passenger on client...");
        }
    }

    public static void send(Entity passenger, Entity vehicle) {
        PacketDistributor.sendToAllPlayers(new AddPassengerPacket(passenger, vehicle));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
