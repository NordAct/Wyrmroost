package com.github.wolfshotz.wyrmroost.items.base;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;

import java.util.List;

/**
 * Helper class used to help register playerArmor items
 */
public class ArmorBase extends ArmorItem
{

    public ArmorBase(Holder<ArmorMaterial> armorMaterialHolder, Type type, Properties properties) {
        super(armorMaterialHolder, type, properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        super.appendHoverText(stack, context, lines, flags);
        lines.add(Component.translatable("item.wyrmroost.armors.set", Component.translatable("item.wyrmroost.armors." + getMaterialName(material)).withStyle(stack.getRarity().getStyleModifier())));

        if (hasDescription())
        {
            lines.add(Component.empty());
            lines.add(Component.translatable(String.format("item.wyrmroost.armors.%s.desc", getMaterialName(material))));
        }
    }

    protected boolean hasDescription()
    {
        return false;
    }

    public void applyFullSetBonus(LivingEntity entity, boolean hasFullSet) {}

    public static boolean hasFullSet(LivingEntity entity) {
        Item prev = null;
        for (ItemStack itemStack : entity.getArmorSlots()) {

            Item item = itemStack.getItem();
            if (prev == null || item.getClass() == prev.getClass()) prev = item;
            else return false;
        }
        return true;
    }

    private String getMaterialName(Holder<ArmorMaterial> material) {
        return material.unwrapKey().map(key -> key.location().getPath()).orElse("");
    }
}
