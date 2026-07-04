package com.github.wolfshotz.wyrmroost.entities.projectile;

import com.github.wolfshotz.wyrmroost.items.GeodeTippedArrowItem;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

public class GeodeTippedArrowEntity extends AbstractArrow implements IEntityWithComplexSpawn {
    private final GeodeTippedArrowItem item;

    public GeodeTippedArrowEntity(EntityType<? extends AbstractArrow> type, Level worldIn)
    {
        super(type, worldIn);
        this.item = (GeodeTippedArrowItem) WRItems.BLUE_GEODE_ARROW.value();
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(item);
    }

    public GeodeTippedArrowEntity(Level worldIn, Item item)
    {
        super((EntityType<? extends AbstractArrow>)WREntities.GEODE_TIPPED_ARROW.value(), worldIn);
        this.item = (GeodeTippedArrowItem) item;
    }

    public GeodeTippedArrowItem getItem() { return item; }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buf) {
        Entity shooter = getOwner();
        buf.writeInt(shooter == null? 0 : shooter.getId());
        buf.writeVarInt(Item.getId(item));
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf registryFriendlyByteBuf) {

    }
}
