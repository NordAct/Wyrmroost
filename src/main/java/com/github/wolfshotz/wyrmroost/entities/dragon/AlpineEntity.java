package com.github.wolfshotz.wyrmroost.entities.dragon;

import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.DragonBreedGoal;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.FlyerWanderGoal;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.MoveToHomeGoal;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.WRFollowOwnerGoal;
import com.github.wolfshotz.wyrmroost.entities.projectile.WindGustEntity;
import com.github.wolfshotz.wyrmroost.entities.util.EntityDataEntry;
import com.github.wolfshotz.wyrmroost.network.packets.AnimationPacket;
import com.github.wolfshotz.wyrmroost.network.packets.KeybindPacket;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import com.github.wolfshotz.wyrmroost.util.TickFloat;
import com.github.wolfshotz.wyrmroost.util.animation.Animation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;

import javax.annotation.Nullable;

public class AlpineEntity extends AbstractDragonEntity
{
    public static final Animation ROAR_ANIMATION = new Animation(84);
    public static final Animation WIND_GUST_ANIMATION = new Animation(25);
    public static final Animation BITE_ANIMATION = new Animation(10);

    public final TickFloat sitTimer = new TickFloat().setLimit(0, 1);
    public final TickFloat flightTimer = new TickFloat().setLimit(0, 1);

    public AlpineEntity(EntityType<? extends AbstractDragonEntity> dragon, Level world)
    {
        super(dragon, world);

        registerDataEntry("Sleeping", EntityDataEntry.BOOLEAN, SLEEPING, false);
        registerDataEntry("Variant", EntityDataEntry.INTEGER, VARIANT, 0);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        goalSelector.addGoal(4, new MoveToHomeGoal(this));
        goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.1d, true));
        goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(7, new DragonBreedGoal(this));
        goalSelector.addGoal(8, new FlyerWanderGoal(this, 1, 0.01f));
        goalSelector.addGoal(9, new LookAtPlayerGoal(this, LivingEntity.class, 10));
        goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        targetSelector.addGoal(0, new HurtByTargetGoal(this));
        targetSelector.addGoal(1, new NonTameRandomTargetGoal<>(this, Bee.class, false, e -> ((Bee) e).hasNectar()));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(FLYING, false);
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        sitTimer.add(isInSittingPose() || isSleeping()? 0.1f : -0.1f);
        sleepTimer.add(isSleeping()? 0.1f : -0.1f);
        flightTimer.add(isFlying()? 0.1f : -0.05f);

        if (!level().isClientSide() && noActiveAnimation() && !isSleeping() && !isBaby() && random.nextDouble() < 0.0005)
            AnimationPacket.send(this, ROAR_ANIMATION);

        Animation animation = getAnimation();
        int tick = getAnimationTick();

        if (animation == ROAR_ANIMATION)
        {
            if (tick == 0) playSound(WRSounds.ENTITY_ALPINE_ROAR.value(), 3f, 1f);
            else if (tick == 25)
            {
                for (LivingEntity entity : getEntitiesNearby(20, e -> e.getType() == WREntities.ALPINE.value()))
                {
                    AlpineEntity alpine = ((AlpineEntity) entity);
                    if (alpine.noActiveAnimation() && alpine.isIdling() && !alpine.isSleeping())
                        alpine.setAnimation(ROAR_ANIMATION);
                }
            }
        }
        else if (animation == WIND_GUST_ANIMATION)
        {
            if (tick == 0) setDeltaMovement(getDeltaMovement().add(0, -0.35, 0));
            if (tick == 4)
            {
                if (!level().isClientSide()) level().addFreshEntity(new WindGustEntity(this));
                setDeltaMovement(getDeltaMovement().add(getLookAngle().reverse().multiply(1.5, 0, 1.5).add(0, 1, 0)));
                playSound(WRSounds.WING_FLAP.value(), 3, 1f, true);
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity enemy)
    {
        boolean flag = super.doHurtTarget(enemy);

        if (!isTame() && flag && !enemy.isAlive() && enemy.getType() == EntityType.BEE)
        {
            Bee bee = (Bee) enemy;
            if (bee.hasNectar() && bee.isLeashed())
            {
                Entity holder = bee.getLeashHolder();
                if (holder instanceof Player) tame(true, (Player) holder);
            }
        }
        return flag;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source)
    {
        Entity attacker = source.getDirectEntity();
        if (attacker != null && attacker.getType() == EntityType.BEE)
        {
            setTarget((Bee) attacker);
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public EntityDimensions getDimensions(Pose poseIn)
    {
        EntityDimensions size = getType().getDimensions().scale(getScale());
        return size.scale(1, isInSittingPose() || isSleeping()? 0.7f : 1).withEyeHeight(getBbHeight() * (isFlying()? 0.8f : 1.25f));
    }

    @Override
    public void recievePassengerKeybind(int key, int mods, boolean pressed)
    {
        if (key == KeybindPacket.MOUNT_KEY2 && pressed && noActiveAnimation() && isFlying())
            setAnimation(WIND_GUST_ANIMATION);
    }

    @Override
    public void setMountCameraAngles(boolean backView, CalculateDetachedCameraDistanceEvent event)
    {
        if (backView) event.getCamera().move(-5f, 0.75f, 0);
        else event.getCamera().move(-3, 0.3f, 0);
    }

    @Override
    public void jumpFromGround()
    {
        super.jumpFromGround();
        if (!level().isClientSide())
            level().addFreshEntity(new WindGustEntity(this, position().add(0, 7, 0), calculateViewVector(90, getYRot())));
    }

    @Override
    protected float getJumpPower()
    {
        if (canFly()) return (getBbHeight() * getBlockJumpFactor());
        else return super.getJumpPower();
    }

    @Override
    public void swing(InteractionHand hand)
    {
        setAnimation(BITE_ANIMATION);
        playSound(SoundEvents.GENERIC_EAT, 1, 1, true);
        super.swing(hand);
    }

    @Override
    public int determineVariant()
    {
        return random.nextInt(6);
    }

    @Override
    protected boolean canRide(Entity entity)
    {
        return !isBaby() && entity instanceof LivingEntity && isOwnedBy((LivingEntity) entity);
    }

    @Override
    public boolean isFoodItem(ItemStack stack) {
        return stack.is(WRItems.Tags.ALPINE_FOOD);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.is(WRItems.Tags.ALPINE_BREEDING_ITEMS);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_ALPINE_IDLE.value();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_ALPINE_ROAR.value();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_ALPINE_DEATH.value();
    }

    @Override
    public Animation[] getAnimations()
    {
        return new Animation[] {ROAR_ANIMATION, WIND_GUST_ANIMATION, BITE_ANIMATION};
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return AbstractDragonEntity.createDragonAttributes()
                .add(Attributes.MAX_HEALTH, 40)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .add(Attributes.ATTACK_DAMAGE, 3)
                .add(Attributes.FLYING_SPEED, 0.185f)
                .add(WREntities.Attributes.PROJECTILE_DAMAGE, 1);
    }
}
