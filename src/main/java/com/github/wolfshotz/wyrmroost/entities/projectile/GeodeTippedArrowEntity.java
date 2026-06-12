package com.github.wolfshotz.wyrmroost.entities.projectile;

import com.github.wolfshotz.wyrmroost.items.GeodeTippedArrowItem;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class GeodeTippedArrowEntity extends AbstractArrowEntity implements IEntityAdditionalSpawnData
{
    private final GeodeTippedArrowItem item;

    public GeodeTippedArrowEntity(EntityType<? extends AbstractArrowEntity> type, Level worldIn)
    {
        super(type, worldIn);
        this.item = (GeodeTippedArrowItem) WRItems.BLUE_GEODE_ARROW.get();
    }

    public GeodeTippedArrowEntity(Level worldIn, Item item)
    {
        super(WREntities.GEODE_TIPPED_ARROW.get(), worldIn);
        this.item = (GeodeTippedArrowItem) item;
    }

    public GeodeTippedArrowEntity(FMLPlayMessages.SpawnEntity packet, Level world)
    {
        super(WREntities.GEODE_TIPPED_ARROW.get(), world);

        PacketBuffer buf = packet.getAdditionalData();
        Entity shooter = world.getEntityByID(buf.readInt());
        if (shooter != null) setShooter(shooter);
        this.item = (GeodeTippedArrowItem) Item.getItemById(buf.readVarInt());
    }

    public GeodeTippedArrowItem getItem() { return item; }

    @Override
    protected ItemStack getArrowStack() { return new ItemStack(item); }

    @Override
    public IPacket<?> createSpawnPacket() { return NetworkHooks.getEntitySpawningPacket(this); }

    @Override
    public void writeSpawnData(PacketBuffer buf)
    {
        Entity shooter = func_234616_v_();
        buf.writeInt(shooter == null? 0 : shooter.getEntityId());
        buf.writeVarInt(Item.getIdFromItem(item));
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {}
}
