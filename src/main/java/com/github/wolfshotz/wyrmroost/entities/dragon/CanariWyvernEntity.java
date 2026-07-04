package com.github.wolfshotz.wyrmroost.entities.dragon;

import com.github.wolfshotz.wyrmroost.client.screen.StaffScreen;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.*;
import com.github.wolfshotz.wyrmroost.entities.util.EntityDataEntry;
import com.github.wolfshotz.wyrmroost.items.staff.StaffAction;
import com.github.wolfshotz.wyrmroost.network.packets.AnimationPacket;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import com.github.wolfshotz.wyrmroost.util.animation.Animation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class CanariWyvernEntity extends AbstractDragonEntity
{
    public static final Animation FLAP_WINGS_ANIMATION = new Animation(22);
    public static final Animation PREEN_ANIMATION = new Animation(36);
    public static final Animation THREAT_ANIMATION = new Animation(40);
    public static final Animation ATTACK_ANIMATION = new Animation(15);

    public Player pissedOffTarget;

    public CanariWyvernEntity(EntityType<? extends AbstractDragonEntity> dragon, Level world)
    {
        super(dragon, world);

        registerDataEntry("Gender", EntityDataEntry.BOOLEAN, GENDER, true);
        registerDataEntry("Sleeping", EntityDataEntry.BOOLEAN, SLEEPING, false);
        registerDataEntry("Variant", EntityDataEntry.INTEGER, VARIANT, 0);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        goalSelector.addGoal(3, new MoveToHomeGoal(this));
        goalSelector.addGoal(4, new AttackGoal());
        goalSelector.addGoal(5, new ThreatenGoal());
        goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(7, new DragonBreedGoal(this));
        goalSelector.addGoal(8, new FlyerWanderGoal(this, 1));
        goalSelector.addGoal(9, new LookAtPlayerGoal(this, LivingEntity.class, 8f));
        goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        targetSelector.addGoal(0, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(2, new DefendHomeGoal(this));
        targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    protected BodyRotationControl createBodyControl()
    {
        return new BodyRotationControl(this);
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        if (!level().isClientSide() && !isPissed() && !isSleeping() && !isFlying() && !isRiding() && noActiveAnimation())
        {
            double rand = random.nextDouble();
            if (rand < 0.001) AnimationPacket.send(this, FLAP_WINGS_ANIMATION);
            else if (rand < 0.002) AnimationPacket.send(this, PREEN_ANIMATION);
        }

        if (getAnimation() == FLAP_WINGS_ANIMATION)
        {
            int tick = getAnimationTick();
            if (tick == 5 || tick == 12) playSound(SoundEvents.PHANTOM_FLAP, 0.7f, 2, true);
            if (!level().isClientSide() && tick == 9 && random.nextDouble() <= 0.25)
                spawnAtLocation(new ItemStack(Items.FEATHER), 0.5f);
        }
        else if (getAnimation() == THREAT_ANIMATION && isPissed())
        {
            setYRot(yBodyRot = yHeadRot = (float) Mafs.getAngle(CanariWyvernEntity.this, pissedOffTarget) - 270f);
        }
    }

    public InteractionResult interactAt(Player player, Vec3 vec3, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        InteractionResult result = super.interactAt(player, vec3, hand);
        if (result.consumesAction()) return result;

        if (!isTame() && isFoodItem(stack) && (isPissed() || player.isCreative() || isBaby()))
        {
            eat(stack);
            if (!level().isClientSide()) tame(random.nextDouble() < 0.2, player);
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        if (isOwnedBy(player) && player.getPassengers().size() < 3 && !player.isShiftKeyDown() && !isLeashed())
        {
            setSit(true);
            setFlying(false);
            clearAI();
            startRiding(player, true);
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean doHurtTarget(Entity entity)
    {
        if (super.doHurtTarget(entity) && entity instanceof LivingEntity)
        {
            int i = 5;
            switch (level().getDifficulty())
            {
                case HARD:
                    i = 15; break;
                case NORMAL:
                    i = 8; break;
                default:
                    break;
            }
            ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.POISON, i * 20));
            return true;
        }
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source)
    {
        return source == damageSources().magic() || super.isInvulnerableTo(source);
    }

    @Override
    public void addScreenInfo(StaffScreen screen)
    {
        super.addScreenInfo(screen);
        screen.addAction(StaffAction.TARGET);
    }

    @Override
    public boolean shouldSleep()
    {
        return !isPissed() && super.shouldSleep();
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
        return WRSounds.ENTITY_CANARI_IDLE.value();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_CANARI_HURT.value();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_CANARI_DEATH.value();
    }

    @Override
    public Animation[] getAnimations()
    {
        return new Animation[] {NO_ANIMATION, FLAP_WINGS_ANIMATION, PREEN_ANIMATION, THREAT_ANIMATION, ATTACK_ANIMATION};
    }

    @Override
    public int determineVariant()
    {
        return random.nextInt(5);
    }

    @Override
    public int getYawRotationSpeed()
    {
        return isFlying()? 12 : 75;
    }

    @Override
    public boolean isFoodItem(ItemStack stack)
    {
        return stack.is(WRItems.Tags.CANARI_WYVERN_FOOD);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.is(WRItems.Tags.CANARI_WYVERN_BREEDING_ITEMS);
    }

    public boolean isPissed()
    {
        return pissedOffTarget != null;
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return AbstractDragonEntity.createDragonAttributes()
                .add(Attributes.MAX_HEALTH, 12)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FLYING_SPEED, 0.1)
                .add(Attributes.ATTACK_DAMAGE, 3);
    }

    public class ThreatenGoal extends Goal
    {
        public Player target;

        public ThreatenGoal()
        {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP, Flag.TARGET));
        }

        @Override
        public boolean canUse()
        {
            if (isTame()) return false;
            if (isFlying()) return false;
            if (getTarget() != null) return false;
            if ((target = level().getNearestPlayer(getX(), getY(), getZ(), 12d, true)) == null)
                return false;
            return canAttack(target);
        }

        @Override
        public void tick()
        {
            double distFromTarget = distanceToSqr(target);
            if (distFromTarget > 30 && !isPissed())
            {
                if (getNavigation().isDone())
                {
                    Vec3 vec3d = LandRandomPos.getPosAway(CanariWyvernEntity.this, 16, 7, target.position());
                    if (vec3d != null) getNavigation().moveTo(vec3d.x, vec3d.y, vec3d.z, 1.5);
                }
            }
            else
            {
                getLookControl().setLookAt(target, 90, 90);
                if (!isPissed())
                {
                    pissedOffTarget = target;
                    AnimationPacket.send(CanariWyvernEntity.this, THREAT_ANIMATION);
                    clearAI();
                }

                if (distFromTarget < 6) setTarget(target);
            }
        }

        @Override
        public void stop()
        {
            target = null;
            pissedOffTarget = null;
        }
    }

    public class AttackGoal extends Goal
    {
        private int repathTimer = 10;
        private int attackDelay = 0;

        public AttackGoal()
        {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
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
            return target != null && target.isAlive() && isWithinRestriction(target.blockPosition()) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target);
        }

        @Override
        public void tick()
        {
            LivingEntity target = getTarget();

            if ((++repathTimer >= 10 || getNavigation().isDone()) && getSensing().hasLineOfSight(target))
            {
                repathTimer = 0;
                if (!isFlying()) setFlying(true);
                getNavigation().moveTo(target.getX(), target.getBoundingBox().maxY - 2, target.getZ(), 1);
                getLookControl().setLookAt(target, 90, 90);
            }

            if (--attackDelay <= 0 && distanceToSqr(target.position().add(0, target.getBoundingBox().getYsize(), 0)) <= 2.25 + target.getBbWidth())
            {
                attackDelay = 20 + random.nextInt(10);
                AnimationPacket.send(CanariWyvernEntity.this, ATTACK_ANIMATION);
                doHurtTarget(target);
            }
        }

        @Override
        public void stop()
        {
            repathTimer = 10;
            attackDelay = 0;
        }
    }
}