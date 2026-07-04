package com.github.wolfshotz.wyrmroost.items;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

public class DragonArmorItem extends ArmorItem {
    public static final ResourceLocation ARMOR_ID = Wyrmroost.rl("dragon_armor");

    public DragonArmorItem(Holder<ArmorMaterial> materialHolder, Item.Properties properties) {
        super(materialHolder, Type.BODY, properties.stacksTo(1));
    }
}
