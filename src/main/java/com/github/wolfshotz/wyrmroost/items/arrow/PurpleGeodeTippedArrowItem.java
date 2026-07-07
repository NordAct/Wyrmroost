package com.github.wolfshotz.wyrmroost.items.arrow;

import com.github.wolfshotz.wyrmroost.entities.projectile.arrow.GeodeTippedArrowEntity;
import com.github.wolfshotz.wyrmroost.entities.projectile.arrow.PurpleGeodeTippedArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class PurpleGeodeTippedArrowItem extends GeodeTippedArrowItem{
    public PurpleGeodeTippedArrowItem(double damage) {
        super(damage);
    }

    @Override
    protected GeodeTippedArrowEntity createGeodeArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack weapon) {
        return new PurpleGeodeTippedArrowEntity(shooter, level, ammo.copyWithCount(1), weapon);
    }
}
