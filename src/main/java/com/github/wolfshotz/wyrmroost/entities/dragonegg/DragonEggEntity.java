package com.github.wolfshotz.wyrmroost.entities.dragonegg;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.items.DragonEggItem;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import com.github.wolfshotz.wyrmroost.util.TickFloat;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.joml.Vector3f;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class DragonEggEntity extends Entity implements IEntityWithComplexSpawn
{
    private static final int HATCH_ID = 1;
    private static final int WIGGLE_ID = 2;
    public static final String DATA_HATCH_TIME = "hatch_time";
    public static final String DATA_DRAGON_TYPE = "dragon_type";
    private static final int UPDATE_CONDITIONS_INTERVAL = 50; // in ticks, this is for performance reasons

    public DragonEggProperties properties; // cache for speed
    public Direction wiggleDirection = Direction.NORTH;
    public TickFloat wiggleTime = new TickFloat().setLimit(0, 1);
    public boolean correctConditions = false;
    public boolean wiggling = false;
    public int hatchTime;

    public DragonEggEntity(EntityType<? extends DragonEggEntity> type, Level world) { super(type, world);}

    public DragonEggEntity(EntityType<AbstractDragonEntity> type, int hatchTime, Level world)
    {
        super(WREntities.DRAGON_EGG.value(), world);
        setContainedDragon(type);
        this.hatchTime = hatchTime;
    }

    // ================================
    //           Entity NBT
    // ================================

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        setContainedDragon(ModUtils.getEntityTypeByKey(compound.getString(DATA_DRAGON_TYPE)));
        hatchTime = compound.getInt(DATA_HATCH_TIME);
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        compound.putString(DATA_DRAGON_TYPE, getDragonKey());
        compound.putInt(DATA_HATCH_TIME, hatchTime);
    }

    public String getDragonKey() { return getContainedDragon().isPresent() ? EntityType.getKey(getContainedDragon().get()).toString() : ""; }
    
    // ================================

    private static final EntityDataAccessor<String> CONTAINED_DRAGON = SynchedEntityData.defineId(DragonEggEntity.class, EntityDataSerializers.STRING);

    public Optional<EntityType<?>> getContainedDragon() {
        return EntityType.byString(entityData.get(CONTAINED_DRAGON));
    }

    public void setContainedDragon(EntityType<?> dragon) {
        entityData.set(CONTAINED_DRAGON, EntityType.getKey(dragon).toString());
    }

    @Override
    public void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(CONTAINED_DRAGON, "");
    }



    @Override
    public void tick()
    {
        if (!level().isClientSide() && !getContainedDragon().isPresent())
        {
            safeError();
            return;
        }

        super.tick();
        updateMotion();

        if (tickCount % UPDATE_CONDITIONS_INTERVAL == 0)
        {
            boolean flag = getProperties().getConditions().test(this);
            if (flag != correctConditions) this.correctConditions = flag;
        }

        if (correctConditions)
        {
            if (level().isClientSide())
            {
                if (tickCount % 3 == 0)
                {
                    double x = getX() + getRandom().nextGaussian() * 0.2d;
                    double y = getY() + getRandom().nextDouble() + getBbHeight() / 2;
                    double z = getZ() + getRandom().nextGaussian() * 0.2d;
                    level().addParticle(new DustParticleOptions(new Vector3f(1f, 1f, 0), 0.5f), x, y, z, 0, 0, 0);
                }
                wiggleTime.add(wiggling? 0.4f : -0.4f);
                if (wiggleTime.get() == 1) wiggling = false;
            }
            else
            {
                if (--hatchTime <= 0)
                {
                    level().broadcastEntityEvent(this, (byte) HATCH_ID); // notify client
                    hatch();
                }
                else if (getRandom().nextInt(Math.max(hatchTime / 2, 5)) == 0)
                    level().broadcastEntityEvent(this, (byte) WIGGLE_ID);
            }
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        if (distance > 3)
        {
            crack(5);
            return true;
        }
        return false;
    }

    private void updateMotion() {
        boolean flag = getDeltaMovement().y <= 0.0D;
        double d1 = getY();
        double d0 = 0.5d;
        
        move(MoverType.SELF, getDeltaMovement());
        if (!isNoGravity() && !isSprinting())
        {
            Vec3 vec3d2 = getDeltaMovement();
            double d2;
            if (flag && Math.abs(vec3d2.y - 0.005D) >= 0.003D && Math.abs(vec3d2.y - d0 / 16.0D) < 0.003D) d2 = -0.003D;
            else d2 = vec3d2.y - d0 / 16.0D;

            setDeltaMovement(vec3d2.x, d2, vec3d2.z);
        }

        Vec3 vec3d6 = getDeltaMovement();
        if (horizontalCollision && isFree(vec3d6.x, vec3d6.y + (double) 0.6F - getY() + d1, vec3d6.z))
        {
            setDeltaMovement(vec3d6.x, 0.3F, vec3d6.z);
        }
    }

    @Override
    public void handleEntityEvent(byte id)
    {
        switch (id)
        {
            case HATCH_ID:
                hatch();
                break;
            case WIGGLE_ID:
                wiggle();
                break;
            default:
                super.handleEntityEvent(id);
        }
    }

    /**
     * Called to hatch the dragon egg
     * Usage: <P>
     * - Get the dragon EntityType (If its something it shouldnt, safely fail) <P>
     * - Set the dragons growing age to a baby <P>
     * - Set the position of that dragon to the position of this egg <P>
     * - Remove this entity (the egg) and play any effects
     */
    public void hatch()
    {
        if (!level().isClientSide())
        {
            getContainedDragon().ifPresent(type -> {
                AbstractDragonEntity newDragon = (AbstractDragonEntity) type.create(level());
                if (newDragon == null) {
                    safeError();
                    return;
                }
                newDragon.setPos(getX(), getY(), getZ());
                newDragon.setAge(getProperties().getGrowthTime());
                newDragon.finalizeSpawn((ServerLevel) level(), level().getCurrentDifficultyAt(getOnPos()), MobSpawnType.BREEDING, null);
                level().addFreshEntity(newDragon);
            });
        }
        else
        {
            crack(25);
            level().playSound(this, blockPosition(), SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 1, 1);
        }
        discard();
    }

    public void crack(int intensity)
    {
        level().playSound(this, blockPosition(), SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 1, 1);
        float f = getBbWidth() * getBbHeight();
        f += f;
        for (int i = 0; i < f * intensity; ++i)
        {
            double x = getX() + (Mafs.nextDouble(getRandom()) * getBbWidth() / 2);
            double y = getY() + (Mafs.nextDouble(getRandom()) * getBbHeight());
            double z = getZ() + (Mafs.nextDouble(getRandom()) * getBbWidth() / 2);
            double xMot = Mafs.nextDouble(getRandom()) * getBbWidth() * 0.35;
            double yMot = getRandom().nextDouble() * getBbHeight() * 0.5;
            double zMot = Mafs.nextDouble(getRandom()) * getBbWidth() * 0.35;
            level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.EGG)), x, y, z, xMot, yMot, zMot);
        }
    }

    public void wiggle()
    {
        if (wiggleTime.get() > 0) return;
        wiggleDirection = Direction.Plane.HORIZONTAL.getRandomDirection(getRandom());
        wiggling = true;
        crack(5);
    }

    /**
     * Called When the dragon type of the egg is not what it should be.
     */
    private void safeError()
    {
        Wyrmroost.LOG.error("THIS ISNT A DRAGON WTF KIND OF ABOMINATION IS THIS HATCHING?!?! Unknown Entity Type for Dragon Egg @ {}", position());
        discard();
    }
    
    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        getContainedDragon().ifPresent(type -> {
            ItemStack stack = DragonEggItem.getStack(type, hatchTime);
            Containers.dropItemStack(level(), getX(), getY(), getZ(), stack);
            discard();
        });

        return true;
    }
    
    public DragonEggProperties getProperties() {
        if (properties == null) return properties = DragonEggProperties.get(getContainedDragon().orElse(null));
        return properties;
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {

        return getContainedDragon().isPresent() ? DragonEggItem.getStack(getContainedDragon().get()) : null;
    }

    @Override
    public EntityDimensions getDimensions(Pose poseIn) { return getProperties().getSize(); }

    public EntityDimensions getSize() { return getProperties().getSize(); }

    @Override
    public boolean isPushable() { return false; }

    @Override
    public boolean canBeCollidedWith() { return true; }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) { buffer.writeCharSequence(getDragonKey(), StandardCharsets.UTF_8); }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buffer) {}
}
