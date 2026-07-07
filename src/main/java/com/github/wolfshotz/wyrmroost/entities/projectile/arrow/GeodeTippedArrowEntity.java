package com.github.wolfshotz.wyrmroost.entities.projectile.arrow;

import com.github.wolfshotz.wyrmroost.items.arrow.GeodeTippedArrowItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

import javax.annotation.Nullable;

public abstract class GeodeTippedArrowEntity extends AbstractArrow implements IEntityWithComplexSpawn {
    public GeodeTippedArrowEntity(EntityType<? extends AbstractArrow> type, Level worldIn) {
        super(type, worldIn);
    }

    public GeodeTippedArrowEntity(EntityType<? extends AbstractArrow> entityType, double x, double y, double z, Level level, ItemStack pickupStack, @Nullable ItemStack weaponStack) {
        super(entityType, x, y, z, level, pickupStack, weaponStack);
    }

    public GeodeTippedArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity shooter, Level level, ItemStack pickupStack, @Nullable ItemStack weaponStack) {
        super(entityType, shooter, level, pickupStack, weaponStack);
    }

    public abstract GeodeTippedArrowItem getItem();

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buf) {
        Entity shooter = getOwner();
        buf.writeInt(shooter == null? 0 : shooter.getId());
        buf.writeVarInt(Item.getId(getItem()));
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf registryFriendlyByteBuf) {

    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(getItem());
    }
}
