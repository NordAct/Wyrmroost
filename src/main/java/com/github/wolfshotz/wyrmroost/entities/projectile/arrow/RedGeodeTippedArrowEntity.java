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

public class RedGeodeTippedArrowEntity extends GeodeTippedArrowEntity {
    public RedGeodeTippedArrowEntity(EntityType<? extends AbstractArrow> type, Level worldIn) {
        super(type, worldIn);
    }

    public RedGeodeTippedArrowEntity(double x, double y, double z, Level level, ItemStack pickupStack, @Nullable ItemStack weaponStack) {
        super((EntityType<? extends AbstractArrow>) WREntities.RED_GEODE_TIPPED_ARROW.value(), x, y, z, level, pickupStack, weaponStack);
    }

    public RedGeodeTippedArrowEntity(LivingEntity shooter, Level level, ItemStack pickupStack, @Nullable ItemStack weaponStack) {
        super((EntityType<? extends AbstractArrow>) WREntities.RED_GEODE_TIPPED_ARROW.value(), shooter, level, pickupStack, weaponStack);
    }

    @Override
    public GeodeTippedArrowItem getItem() {
        return (GeodeTippedArrowItem) WRItems.RED_GEODE_ARROW.value();
    }
}
