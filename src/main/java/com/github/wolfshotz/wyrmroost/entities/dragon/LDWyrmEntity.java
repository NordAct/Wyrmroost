package com.github.wolfshotz.wyrmroost.entities.dragon;

import com.github.wolfshotz.wyrmroost.registry.WRDataComponentTypes;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import com.github.wolfshotz.wyrmroost.util.animation.Animation;
import com.github.wolfshotz.wyrmroost.util.animation.IAnimatable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * Desertwyrm Dragon Entity
 * Seperated from AbstractDragonEntity:
 * This does not need/require much from that class and would instead create redundancies. do this instead.
 */
public class LDWyrmEntity extends PathfinderMob implements IAnimatable
{
    public static final String DATA_BURROWED = "Burrowed";
    public static final Animation BITE_ANIMATION = new Animation(10);
    private static final EntityDataAccessor<Boolean> BURROWED = SynchedEntityData.defineId(LDWyrmEntity.class, EntityDataSerializers.BOOLEAN);
    private static final Predicate<LivingEntity> AVOIDING = t -> EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(t) && !(t instanceof LDWyrmEntity);

    public Animation animation = NO_ANIMATION;
    public int animationTick;

    public LDWyrmEntity(EntityType<? extends LDWyrmEntity> minutus, Level world)
    {
        super(minutus, world);
    }

    @Override
    protected void registerGoals()
    {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new AvoidEntityGoal<>(this, LivingEntity.class, 6f, 0.8d, 1.2d, AVOIDING));
        goalSelector.addGoal(3, new BurrowGoal());
        goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1));
    }

    @Override
    public ItemStack getPickedResult(HitResult target)
    {
        return new ItemStack(SpawnEggItem.byId(getType()));
    }

    // ================================
    //           Entity NBT
    // ================================
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(BURROWED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putBoolean(DATA_BURROWED, isBurrowed());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        setBurrowed(compound.getBoolean(DATA_BURROWED));
    }

    /**
     * Whether or not the Minutus is burrowed
     */
    public boolean isBurrowed()
    {
        return entityData.get(BURROWED);
    }

    public void setBurrowed(boolean burrow)
    {
        entityData.set(BURROWED, burrow);
    }

    // ================================

    @Override
    public void aiStep()
    {
        super.aiStep();

        if (isBurrowed()) {
            if (!level().getBlockState(blockPosition().below(1)).is(Tags.Blocks.SANDS)) setBurrowed(false);
            attackAbove();
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick()
    {
        super.tick();

        if (getAnimation() != NO_ANIMATION)
        {
            ++animationTick;
            if (animationTick >= animation.getDuration()) setAnimation(NO_ANIMATION);
        }
    }

    private void attackAbove()
    {
        Predicate<Entity> predicateFilter = filter ->
        {
            if (filter instanceof LDWyrmEntity) return false;
            return filter instanceof FishingHook || (filter instanceof LivingEntity && filter.getBbWidth() < 0.9f && filter.getBbHeight() < 0.9f);
        };
        AABB aabb = getBoundingBox().inflate(0, 2, 0).inflate(0.5, 0, 0.5);
        List<Entity> entities = level().getEntities(this, aabb, predicateFilter);
        if (entities.isEmpty()) return;

        Optional<Entity> closest = entities.stream().min(Comparator.comparingDouble(entity -> entity.distanceTo(this)));
        Entity entity = closest.get();
        if (entity instanceof FishingHook)
        {
            entity.discard();
            setDeltaMovement(0, 0.8, 0);
            setBurrowed(false);
        }
        else
        {
            if (getAnimation() != BITE_ANIMATION) setAnimation(BITE_ANIMATION);
            doHurtTarget(entity);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        if (player.getItemInHand(hand).isEmpty())
        {
            if (!level().isClientSide())
            {
                ItemStack stack = new ItemStack(WRItems.LDWYRM.value());
                CompoundTag tag = new CompoundTag();
                save(tag);
                if (hasCustomName()) stack.set(DataComponents.CUSTOM_NAME, getCustomName());
                stack.set(WRDataComponentTypes.DRAGON_TAG_COMPONENT, tag);
                Containers.dropItemStack(level(), getX(), getY(), getZ(), stack);
                discard();
            }
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader world) // Attracted to sand
    {
        if (world.getBlockState(pos).is(Tags.Blocks.SANDS)) return 10f;
        return super.getWalkTargetValue(pos, world);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer)
    {
        return true;
    }

    @Override
    public void checkDespawn()
    {
        if (isPersistenceRequired())
        {
            noActionTime = 0;
            return;
        }

        if (EventHooks.checkMobDespawn(this)) return;

        Entity player = level().getNearestPlayer(this, 32);
        if (player == null && (random.nextDouble() < 0.0075 || !level().isDay())) discard();
        else noActionTime = 0;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source)
    {
        return super.isInvulnerableTo(source) || source == damageSources().inWall();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_LDWYRM_IDLE.value();
    }

//    @Nullable
//    @Override
//    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
//    {
////        return WRSounds.MINUTUS_SCREECH.get();
//    }

    @Override
    protected float getSoundVolume()
    {
        return 0.15f;
    }

    @Override
    public boolean isPushable()
    {
        return !isBurrowed();
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return !isBurrowed();
    }

    @Override
    protected void doPush(Entity entityIn)
    {
        if (!isBurrowed()) super.doPush(entityIn);
    }

    @Override
    protected boolean isImmobile()
    {
        return super.isImmobile() || isBurrowed();
    }

    @Override
    public int getAnimationTick()
    {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick)
    {
        animationTick = tick;
    }

    @Override
    public Animation getAnimation()
    {
        return animation;
    }

    @Override
    public Animation[] getAnimations()
    {
        return new Animation[] {NO_ANIMATION, BITE_ANIMATION};
    }

    @Override
    public void setAnimation(Animation animation)
    {
        this.animation = animation;
        setAnimationTick(0);
    }

    public static <F extends Mob> boolean getSpawnPlacement(EntityType<F> fEntityType, ServerLevelAccessor world, MobSpawnType reason, BlockPos pos, RandomSource random)
    {
        if (reason == MobSpawnType.SPAWNER) return true;
        Block block = world.getBlockState(pos.below()).getBlock();
        return block == Blocks.SAND && world.getRawBrightness(pos, 0) > 8;
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.ATTACK_DAMAGE, 4);
    }

    class BurrowGoal extends Goal
    {
        private int burrowTicks = 30;

        public BurrowGoal()
        {
            setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        @Override
        public boolean canUse()
        {
            return !isBurrowed() && belowIsSand();
        }

        @Override
        public void stop()
        {
            burrowTicks = 30;
        }

        @Override
        public void tick()
        {
            if (--burrowTicks <= 0)
            {
                setBurrowed(true);
                burrowTicks = 30;
            }
        }

        private boolean belowIsSand() {
            return level().getBlockState(blockPosition().below(1)).is(Tags.Blocks.SANDS);
        }
    }
}
