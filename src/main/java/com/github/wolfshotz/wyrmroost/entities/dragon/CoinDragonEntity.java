package com.github.wolfshotz.wyrmroost.entities.dragon;

import com.github.wolfshotz.wyrmroost.registry.WRDataComponentTypes;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * Simple Entity really, just bob and down in the same spot, and land to sleep at night. Easy.
 */
public class CoinDragonEntity extends Mob
{
    public static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(CoinDragonEntity.class, EntityDataSerializers.INT);
    public static String DATA_VARIANT = "Variant";

    public CoinDragonEntity(EntityType<? extends CoinDragonEntity> type, Level worldIn)
    {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals()
    {
        goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 4));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        entityData.set(VARIANT, random.nextInt(5));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putInt(DATA_VARIANT, getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        setVariant(compound.getInt(DATA_VARIANT));
    }

    public int getVariant()
    {
        return entityData.get(VARIANT);
    }

    public void setVariant(int variant)
    {
        entityData.set(VARIANT, variant);
    }

    // move up if too low, move down if too high, else, just bob up and down
    @Override
    public void travel(Vec3 positionIn)
    {
        if (isNoAi()) return;
        double moveSpeed = 0.02;
        double yMot;
        double altitiude = getAltitude();
        if (altitiude < 1.5) yMot = moveSpeed;
        else if (altitiude > 3) yMot = -moveSpeed;
        else yMot = Math.sin(tickCount * 0.1) * 0.0035;

        setDeltaMovement(getDeltaMovement().add(0, yMot, 0));
        move(MoverType.SELF, getDeltaMovement());
        setDeltaMovement(getDeltaMovement().scale(0.91));
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        InteractionResult stackResult = player.getItemInHand(hand).interactLivingEntity(player, this, hand);
        if (stackResult.consumesAction()) return stackResult;

        net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(level(), getX(), getY(), getZ(), getItemStack());
        double x = player.getX() - getX();
        double y = player.getY() - getY();
        double z = player.getZ() - getZ();
        itemEntity.setDeltaMovement(x * 0.1, y * 0.1 + Math.sqrt(Math.sqrt(x * x + y * y + z * z)) * 0.08, z * 0.1);
        level().addFreshEntity(itemEntity);
        discard();
        return InteractionResult.sidedSuccess(level().isClientSide());
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).withEyeHeight(getBbHeight() * 0.8645f);
    }

    @Override
    public ItemStack getPickedResult(HitResult target)
    {
        return new ItemStack(WRItems.COIN_DRAGON.value());
    }

    @Override
    public boolean requiresCustomPersistence()
    {
        return true;
    }

    @Override
    public boolean onClimbable()
    {
        return false;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource)
    {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos)
    {
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_COINDRAGON_IDLE.value();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_COINDRAGON_IDLE.value();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_COINDRAGON_IDLE.value();
    }

    public double getAltitude()
    {
        BlockPos.MutableBlockPos pos = blockPosition().mutable().move(0, -1, 0);
        while (pos.getY() > 0 && !level().getBlockState(pos).isSolid()) pos.setY(pos.getY() - 1);
        return getY() - pos.getY();
    }

    public ItemStack getItemStack()
    {
        ItemStack stack = new ItemStack(WRItems.COIN_DRAGON.value());
        CompoundTag dragonTag = new CompoundTag();
        save(dragonTag);
        stack.set(WRDataComponentTypes.DRAGON_TAG_COMPONENT, dragonTag);
        if (hasCustomName()) stack.set(DataComponents.CUSTOM_NAME, getCustomName());
        return stack;
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 4).add(Attributes.MOVEMENT_SPEED, 0.02);
    }
}