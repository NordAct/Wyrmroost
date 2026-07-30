package com.github.wolfshotz.wyrmroost.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExtraCodecs;

public record ShoulderDragon(int ordinal, CompoundTag nbt) {
    public static final Codec<ShoulderDragon> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("ordinal").forGetter(ShoulderDragon::ordinal),
            CompoundTag.CODEC.fieldOf("nbt").forGetter(ShoulderDragon::nbt)
    ).apply(i, ShoulderDragon::new));
}
