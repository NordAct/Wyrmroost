package com.github.wolfshotz.wyrmroost.items;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.items.base.ArmorBase;
import com.github.wolfshotz.wyrmroost.registry.WRArmorMaterials;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class DrakeArmorItem extends ArmorBase
{
    private static final ResourceLocation KB_RESISTANCE_ID = Wyrmroost.rl("drake_set");
    private static final AttributeModifier KB_RESISTANCE = new AttributeModifier(KB_RESISTANCE_ID, 10, AttributeModifier.Operation.ADD_VALUE);

    public DrakeArmorItem(Type type, Properties properties) {
        super(WRArmorMaterials.DRAKE, type, properties);
    }

    @Override
    public void applyFullSetBonus(LivingEntity entity, boolean hasFullSet)
    {
        AttributeInstance attribute = entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (attribute.hasModifier(KB_RESISTANCE_ID)) attribute.removeModifier(KB_RESISTANCE_ID);
        if (hasFullSet) attribute.addOrUpdateTransientModifier(KB_RESISTANCE);
    }

    @Override
    protected boolean hasDescription()
    {
        return true;
    }
}
