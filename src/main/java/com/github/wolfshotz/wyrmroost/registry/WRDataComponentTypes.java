package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragonegg.DragonEggEntity;
import com.github.wolfshotz.wyrmroost.items.staff.DragonStaffItem;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class WRDataComponentTypes {
    public static final DeferredRegister<DataComponentType<?>> REGISTRY = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Wyrmroost.MOD_ID);

    public static final Supplier<DataComponentType<Integer>> ACTION_COMPONENT = register(DragonStaffItem.DATA_ACTION, ByteBufCodecs.INT, null);
    public static final Supplier<DataComponentType<Integer>> DRAGON_ID_COMPONENT = register(DragonStaffItem.DATA_DRAGON_ID, ByteBufCodecs.INT, null);
    public static final Supplier<DataComponentType<Integer>> HATCH_TIME_COMPONENT = register(DragonEggEntity.DATA_HATCH_TIME, ByteBufCodecs.INT, Codec.INT);
    public static final Supplier<DataComponentType<String>> DRAGON_TYPE_COMPONENT = register(DragonEggEntity.DATA_DRAGON_TYPE, ByteBufCodecs.STRING_UTF8, Codec.STRING);
    public static final Supplier<DataComponentType<CompoundTag>> DRAGON_TAG_COMPONENT = register("dragon_tag", ByteBufCodecs.COMPOUND_TAG, CompoundTag.CODEC);

    private static <T> Supplier<DataComponentType<T>> register(String name, @Nullable StreamCodec<ByteBuf, T> streamCodec, @Nullable Codec<T> codec) {
        return REGISTRY.register(
                name,
                () -> {
                    DataComponentType.Builder<T> builder = DataComponentType.builder();
                    if (streamCodec != null) builder.networkSynchronized(streamCodec).build();
                    if (codec != null) builder.persistent(codec);
                    return builder.build();
                }
        );
    }
}
