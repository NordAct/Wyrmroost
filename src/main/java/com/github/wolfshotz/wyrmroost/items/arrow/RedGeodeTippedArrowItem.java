package com.github.wolfshotz.wyrmroost.items.arrow;

import com.github.wolfshotz.wyrmroost.entities.projectile.arrow.GeodeTippedArrowEntity;
import com.github.wolfshotz.wyrmroost.entities.projectile.arrow.RedGeodeTippedArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class RedGeodeTippedArrowItem extends GeodeTippedArrowItem{
    public RedGeodeTippedArrowItem(double damage) {
        super(damage);
    }

    @Override
    protected GeodeTippedArrowEntity createGeodeArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack weapon) {
        return new RedGeodeTippedArrowEntity(shooter, level, ammo.copyWithCount(1), weapon);
    }
}
