package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.attachments.ShoulderDragon;
import com.google.common.base.Predicates;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class WRAttachments {
    public static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Wyrmroost.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<List<ShoulderDragon>>> SHOULDER_DRAGON_LIST = REGISTRY.register(
            "shoulder_dragon_list", () -> AttachmentType.builder(() -> (List<ShoulderDragon>) new ArrayList<ShoulderDragon>()).serialize(ShoulderDragon.CODEC.listOf(), Predicates.alwaysTrue()).build()
    );
}
