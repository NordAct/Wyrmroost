package com.github.wolfshotz.wyrmroost.items.arrow;

import com.github.wolfshotz.wyrmroost.entities.projectile.arrow.BlueGeodeTippedArrowEntity;
import com.github.wolfshotz.wyrmroost.entities.projectile.arrow.GeodeTippedArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BlueGeodeTippedArrowItem extends GeodeTippedArrowItem{
    public BlueGeodeTippedArrowItem(double damage) {
        super(damage);
    }

    @Override
    protected GeodeTippedArrowEntity createGeodeArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack weapon) {
        return new BlueGeodeTippedArrowEntity(shooter, level, ammo.copyWithCount(1), weapon);
    }
}
