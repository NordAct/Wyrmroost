package com.github.wolfshotz.wyrmroost.entities.projectile.arrow;

import com.github.wolfshotz.wyrmroost.items.arrow.GeodeTippedArrowItem;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class PurpleGeodeTippedArrowEntity extends GeodeTippedArrowEntity {
    public PurpleGeodeTippedArrowEntity(EntityType<? extends AbstractArrow> type, Level worldIn) {
        super(type, worldIn);
    }

    public PurpleGeodeTippedArrowEntity(double x, double y, double z, Level level, ItemStack pickupStack, @Nullable ItemStack weaponStack) {
        super((EntityType<? extends AbstractArrow>) WREntities.PURPLE_GEODE_TIPPED_ARROW.value(), x, y, z, level, pickupStack, weaponStack);
    }

    public PurpleGeodeTippedArrowEntity(LivingEntity shooter, Level level, ItemStack pickupStack, @Nullable ItemStack weaponStack) {
        super((EntityType<? extends AbstractArrow>) WREntities.PURPLE_GEODE_TIPPED_ARROW.value(), shooter, level, pickupStack, weaponStack);
    }

    @Override
    public GeodeTippedArrowItem getItem() {
        return (GeodeTippedArrowItem) WRItems.PURPLE_GEODE_ARROW.value();
    }
}
