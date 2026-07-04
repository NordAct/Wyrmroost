package com.github.wolfshotz.wyrmroost.entities.dragon;

import com.github.wolfshotz.wyrmroost.WRConfig;
import com.github.wolfshotz.wyrmroost.client.screen.StaffScreen;
import com.github.wolfshotz.wyrmroost.client.sounds.BreathSound;
import com.github.wolfshotz.wyrmroost.containers.DragonInvContainer;
import com.github.wolfshotz.wyrmroost.containers.util.SlotBuilder;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.DragonInvHandler;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.LessShitLookController;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.*;
import com.github.wolfshotz.wyrmroost.entities.projectile.breath.FireBreathEntity;
import com.github.wolfshotz.wyrmroost.entities.util.EntityDataEntry;
import com.github.wolfshotz.wyrmroost.items.DragonArmorItem;
import com.github.wolfshotz.wyrmroost.items.staff.StaffAction;
import com.github.wolfshotz.wyrmroost.network.packets.AnimationPacket;
import com.github.wolfshotz.wyrmroost.network.packets.KeybindPacket;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import com.github.wolfshotz.wyrmroost.util.TickFloat;
import com.github.wolfshotz.wyrmroost.util.animation.Animation;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class RoyalRedEntity extends AbstractDragonEntity
{
    public static final int ARMOR_SLOT = 0;

    public static final Animation ROAR_ANIMATION = new Animation(70);
    public static final Animation SLAP_ATTACK_ANIMATION = new Animation(30);
    public static final Animation BITE_ATTACK_ANIMATION = new Animation(15);

    public static final EntityDataAccessor<Boolean> BREATHING_FIRE = SynchedEntityData.defineId(RoyalRedEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> KNOCKED_OUT = SynchedEntityData.defineId(RoyalRedEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int MAX_KNOCKOUT_TIME = 3600; // 3 minutes

    public final TickFloat flightTimer = new TickFloat().setLimit(0, 1);
    public final TickFloat sitTimer = new TickFloat().setLimit(0, 1);
    public final TickFloat breathTimer = new TickFloat().setLimit(0, 1);
    public final TickFloat knockOutTimer = new TickFloat().setLimit(0, 1);
    private int knockOutTime = 0;

    public RoyalRedEntity(EntityType<? extends AbstractDragonEntity> dragon, Level world)
    {
        super(dragon, world);
        noCulling = WRConfig.disableFrustumCheck;

        setPathfindingMalus(PathType.DANGER_FIRE, 0);
        setPathfindingMalus(PathType.DAMAGE_FIRE, 0);

        registerDataEntry("Gender", EntityDataEntry.BOOLEAN, GENDER, random.nextBoolean());
        registerDataEntry("Sleeping", EntityDataEntry.BOOLEAN, SLEEPING, false);
        registerDataEntry("Variant", EntityDataEntry.INTEGER, VARIANT, 0);
        registerDataEntry("KnockOutTime", EntityDataEntry.INTEGER, () -> knockOutTime, this::setKnockoutTime);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        builder.define(BREATHING_FIRE, false);
        builder.define(KNOCKED_OUT, false);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        goalSelector.addGoal(4, new MoveToHomeGoal(this));
        goalSelector.addGoal(5, new AttackGoal());
        goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(7, new DragonBreedGoal(this));
        goalSelector.addGoal(9, new FlyerWanderGoal(this, 1));
        goalSelector.addGoal(10, new LookAtPlayerGoal(this, LivingEntity.class, 10f));
        goalSelector.addGoal(11, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new DefendHomeGoal(this));
        targetSelector.addGoal(4, new HurtByTargetGoal(this));
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, false, e -> e.getType() == EntityType.PLAYER || e instanceof Animal));
    }

    @Override
    public DragonInvHandler createInv()
    {
        return new DragonInvHandler(this, 1);
    }

    @Override
    public void aiStep()
    {
        super.aiStep();
        flightTimer.add(isFlying()? 0.1f : -0.05f);
        sitTimer.add(isInSittingPose()? 0.1f : -0.1f);
        sleepTimer.add(isSleeping()? 0.035f : -0.1f);
        breathTimer.add(isBreathingFire()? 0.15f : -0.2f);
        knockOutTimer.add(isKnockedOut()? 0.05f : -0.1f);

        if (!level().isClientSide())
        {
            if (isBreathingFire() && getControllingPlayer() == null && getTarget() == null)
                setBreathingFire(false);

            if (breathTimer.get() == 1) level().addFreshEntity(new FireBreathEntity(this));

            if (noActiveAnimation() && !isKnockedOut() && !isSleeping() && !isBreathingFire() && !isBaby() && random.nextDouble() < 0.0004)
                AnimationPacket.send(this, ROAR_ANIMATION);

            if (isKnockedOut() && --knockOutTime <= 0) setKnockedOut(false);
        }

        Animation anim = getAnimation();
        int animTime = getAnimationTick();

        if (anim == ROAR_ANIMATION)
        {
            if (animTime == 0) playSound(WRSounds.ENTITY_ROYALRED_ROAR.value(), 6, 1, true);
            ((LessShitLookController) getLookControl()).restore();
            for (LivingEntity entity : getEntitiesNearby(10, this::isAlliedTo))
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60));
        }
        else if (anim == SLAP_ATTACK_ANIMATION && (animTime == 7 || animTime == 12))
        {
            attackInBox(getOffsetBox(getBbWidth()).inflate(0.2), 50);
            if (animTime == 7) playSound(WRSounds.ENTITY_ROYALRED_HURT.value(), 1, 1, true);
            setYRot(yHeadRot);
        }
        else if (anim == BITE_ATTACK_ANIMATION && animTime == 4)
        {
            attackInBox(getOffsetBox(getBbWidth()).inflate(-0.3), 100);
            playSound(WRSounds.ENTITY_ROYALRED_HURT.value(), 1, 1, true);
        }
    }

    @Override
    public InteractionResult actuallyInteractWithMob(Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if (!isTame() && isFoodItem(stack))
        {
            if (isBaby() || player.isCreative())
            {
                eat(stack);
                tame(random.nextDouble() < 0.1, player);
                setKnockedOut(false);
                return InteractionResult.sidedSuccess(level().isClientSide());
            }

            if (isKnockedOut() && knockOutTime <= MAX_KNOCKOUT_TIME / 2)
            {
                if (!level().isClientSide())
                {
                    // base taming chances on consciousness; the closer it is to waking up the better the chances
                    if (tame(random.nextInt(knockOutTime) < MAX_KNOCKOUT_TIME * 0.2d, player))
                    {
                        setKnockedOut(false);
                        AnimationPacket.send(this, ROAR_ANIMATION);
                    }
                    else knockOutTime += 600; // add 30 seconds to knockout time
                    eat(stack);
                    player.swing(hand);
                    return InteractionResult.SUCCESS;
                }
                else return InteractionResult.CONSUME;
            }
        }

        return super.actuallyInteractWithMob(player, hand);
    }

    @Override
    public void die(DamageSource cause)
    {
        if (isTame() || isKnockedOut() || cause.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
            super.die(cause);
        else // knockout RR's instead of killing them
        {
            setHealth(getMaxHealth() * 0.25f); // reset to 25% health
            setKnockedOut(true);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key)
    {
        if (level().isClientSide() && key.equals(BREATHING_FIRE) && isBreathingFire())
            BreathSound.play(this);
        else super.onSyncedDataUpdated(key);
    }

    @Override
    public void onInvContentsChanged(int slot, ItemStack stack, boolean onLoad)
    {
        if (slot == ARMOR_SLOT) setArmor(stack);
    }

    @Override
    public void addContainerInfo(DragonInvContainer container)
    {
        super.addContainerInfo(container);
        container.addSlot(new SlotBuilder(container.inventory, ARMOR_SLOT).only(DragonArmorItem.class));
    }

    @Override
    public void recievePassengerKeybind(int key, int mods, boolean pressed)
    {
        if (!noActiveAnimation()) return;

        if (key == KeybindPacket.MOUNT_KEY1 && pressed && !isBreathingFire())
        {
            if ((mods & GLFW.GLFW_MOD_CONTROL) != 0) setAnimation(ROAR_ANIMATION);
            else meleeAttack();
        }

        if (key == KeybindPacket.MOUNT_KEY2) setBreathingFire(pressed);
    }

    public void meleeAttack()
    {
        if (!level().isClientSide())
            AnimationPacket.send(this, isFlying() || random.nextBoolean()? BITE_ATTACK_ANIMATION : SLAP_ATTACK_ANIMATION);
    }

    @Override
    public Vec3 getApproximateMouthPos()
    {
        Vec3 position = getEyePosition(1).subtract(0, 1.3d, 0);
        position = position.add(calculateViewVector(getXRot(), yBodyRot).scale(getBbWidth() / 2)); // base of neck
        return position.add(calculateViewVector(getXRot(), yHeadRot).scale(2.75));
    }

    @Override
    public EntityDimensions getDimensions(Pose poseIn)
    {
        EntityDimensions size = getType().getDimensions().scale(getScale());
        float heightFactor = isSleeping()? 0.5f : isInSittingPose()? 0.9f : 1;
        return size.scale(1, heightFactor).withEyeHeight(getBbHeight() * (isFlying()? 0.95f : 1.13f));
    }

    @Override
    public void addScreenInfo(StaffScreen screen)
    {
        screen.addAction(StaffAction.INVENTORY);
        screen.addAction(StaffAction.TARGET);
        super.addScreenInfo(screen);
    }

    @Override
    public void setMountCameraAngles(boolean backView, CalculateDetachedCameraDistanceEvent event)
    {
        if (backView) event.getCamera().move(-8.5f, 3f, 0);
        else event.getCamera().move(-5, -0.75f, 0);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source)
    {
        return source == damageSources().inWall()|| super.isInvulnerableTo(source);
    }

    @Override
    public int determineVariant()
    {
        return random.nextDouble() < 0.03? -1 : 0;
    }

    @Override
    protected boolean isImmobile()
    {
        return super.isImmobile() || isKnockedOut();
    }

    @Override
    protected boolean canRide(Entity entity)
    {
        return !isBaby() && !isKnockedOut() && isTame();
    }

    @Override
    protected boolean canAddPassenger(Entity passenger)
    {
        return getPassengers().size() < 3;
    }

    @Override
    public Vec3 getPassengerPosOffset(Entity entity, int index)
    {
        return new Vec3(0, -0.85f, index == 0? 0.5f : -1);
    }

    @Override
    public float sanitizeScale(float scale)
    {
        return super.sanitizeScale(scale) * (isBaby() ? 0.3f : isMale()? 0.8f : 1f);
    }

    @Override
    public int getYawRotationSpeed()
    {
        return isFlying()? 5 : 7;
    }

    public boolean isBreathingFire()
    {
        return entityData.get(BREATHING_FIRE);
    }

    public void setBreathingFire(boolean b)
    {
        if (!level().isClientSide()) entityData.set(BREATHING_FIRE, b);
    }

    public boolean isKnockedOut()
    {
        return entityData.get(KNOCKED_OUT);
    }

    public void setKnockedOut(boolean b)
    {
        entityData.set(KNOCKED_OUT, b);
        if (!level().isClientSide())
        {
            knockOutTime = b? MAX_KNOCKOUT_TIME : 0;
            if (b)
            {
                yHeadRot = getYRot();
                clearAI();
                setFlying(false);
            }
        }
    }

    public void setKnockoutTime(int i)
    {
        knockOutTime = Math.max(0, i);
        if (i > 0 && !isKnockedOut()) entityData.set(KNOCKED_OUT, true);
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source)
    {
        if (isKnockedOut()) return false;
        return super.causeFallDamage(distance, damageMultiplier, source);
    }

    @Override
    public boolean canFly()
    {
        return super.canFly() && !isKnockedOut();
    }

    @Override
    public boolean isImmuneToArrows()
    {
        return true;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean isFoodItem(ItemStack stack)
    {
        return stack.is(WRItems.Tags.ROYAL_RED_FOOD);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.is(WRItems.Tags.ROYAL_RED_BREEDING_ITEMS);
    }

    @Override
    public boolean shouldRender(double x, double y, double z)
    {
        return true;
    }

    @Override
    public boolean shouldSleep()
    {
        return !isKnockedOut() && super.shouldSleep();
    }

    @Override
    public boolean defendsHome()
    {
        return true;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_ROYALRED_IDLE.value();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_ROYALRED_HURT.value();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_ROYALRED_DEATH.value();
    }

    @Override
    public Animation[] getAnimations()
    {
        return new Animation[] {NO_ANIMATION, ROAR_ANIMATION, SLAP_ATTACK_ANIMATION, BITE_ATTACK_ANIMATION};
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void applyAttributes()
    {
        if (!isMale())
        {
            // base female attributes
            getAttribute(Attributes.MAX_HEALTH).setBaseValue(130);
            getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.22);
            getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(4);
            getAttribute(Attributes.FLYING_SPEED).setBaseValue(0.121);
        }
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        // base male attributes
        return AbstractDragonEntity.createDragonAttributes()
                .add(Attributes.MAX_HEALTH, 120)
                .add(Attributes.MOVEMENT_SPEED, 0.2275)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .add(Attributes.FOLLOW_RANGE, 60)
                .add(Attributes.ATTACK_KNOCKBACK, 3)
                .add(Attributes.ATTACK_DAMAGE, 12)
                .add(Attributes.FLYING_SPEED, 0.125)
                .add(WREntities.Attributes.PROJECTILE_DAMAGE, 4);
    }

    class AttackGoal extends Goal
    {
        public AttackGoal()
        {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse()
        {
            LivingEntity target = getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse()
        {
            LivingEntity target = getTarget();
            if (target != null && target.isAlive())
            {
                if (!isWithinRestriction(target.blockPosition())) return false;
                return EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target);
            }
            return false;
        }

        @Override
        public void tick()
        {
            LivingEntity target = getTarget();
            double distFromTarget = distanceToSqr(target);
            double degrees = Math.atan2(target.getZ() - getZ(), target.getX() - getX()) * (180 / Math.PI) - 90;
            boolean isBreathingFire = isBreathingFire();
            boolean canSeeTarget = getSensing().hasLineOfSight(target);

            getLookControl().setLookAt(target, 90, 90);

            double headAngle = Math.abs(Mth.wrapDegrees(degrees - yHeadRot));
            boolean shouldBreatheFire = (!hasRestriction() || !isWithinRestriction()) && (distFromTarget > 100 || target.getY() - getY() > 3 || isFlying()) && headAngle < 30;
            if (isBreathingFire != shouldBreatheFire) setBreathingFire(isBreathingFire = shouldBreatheFire);

            if (random.nextDouble() < 0.001 || distFromTarget > 900) setFlying(true);
            else if (distFromTarget <= 24 && noActiveAnimation() && !isBreathingFire && canSeeTarget)
            {
                setYRot(yBodyRot = (float) Mafs.getAngle(RoyalRedEntity.this, target) + 90);
                meleeAttack();
            }

            if (getNavigation().isDone() || tickCount % 10 == 0)
            {
                boolean isFlyingTarget = target instanceof AbstractDragonEntity && ((AbstractDragonEntity) target).isFlying();
                double y = target.getY() + (!isFlyingTarget && random.nextDouble() > 0.1? 8 : 0);
                getNavigation().moveTo(target.getX(), y, target.getZ(), !isFlying() && isBreathingFire? 0.8d : 1.3d);
            }
        }
    }
}
