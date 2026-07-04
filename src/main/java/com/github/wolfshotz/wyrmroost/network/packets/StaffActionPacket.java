package com.github.wolfshotz.wyrmroost.network.packets;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.items.staff.DragonStaffItem;
import com.github.wolfshotz.wyrmroost.items.staff.StaffAction;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record StaffActionPacket(StaffAction action) implements CustomPacketPayload {
    public static final Type<StaffActionPacket> TYPE = new Type<>(Wyrmroost.rl("staff_action"));
    public static final StreamCodec<ByteBuf, StaffActionPacket> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> payload.encode(buf),
            StaffActionPacket::new
    );
    public StaffActionPacket(ByteBuf buf) {
        this(StaffAction.VALUES[buf.readInt()]);
    }

    public void encode(ByteBuf buf) { buf.writeInt(action.ordinal()); }

    public boolean handle(IPayloadContext context)
    {
        Player player = context.player();
        ItemStack stack = ModUtils.getHeldStack(player, WRItems.DRAGON_STAFF.value());
        if (stack != null)
        {
            DragonStaffItem.setAction(action, player, stack);
            return true;
        }
        return false;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
