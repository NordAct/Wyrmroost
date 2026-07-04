package com.github.wolfshotz.wyrmroost.items;

import com.github.wolfshotz.wyrmroost.entities.projectile.GeodeTippedArrowEntity;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class GeodeTippedArrowItem extends ArrowItem
{
    private final double damage;

    public GeodeTippedArrowItem(double damage)
    {
        super(WRItems.builder());
        this.damage = damage;
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack weapon) {
        GeodeTippedArrowEntity arrow = new GeodeTippedArrowEntity(level, this);
        arrow.setOwner(shooter);
        arrow.setBaseDamage(damage);
        return arrow;
    }
}
