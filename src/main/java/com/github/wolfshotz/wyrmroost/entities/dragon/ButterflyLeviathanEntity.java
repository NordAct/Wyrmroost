package com.github.wolfshotz.wyrmroost.entities.dragon;

import com.github.wolfshotz.wyrmroost.WRConfig;
import com.github.wolfshotz.wyrmroost.client.screen.StaffScreen;
import com.github.wolfshotz.wyrmroost.containers.DragonInvContainer;
import com.github.wolfshotz.wyrmroost.containers.util.SlotBuilder;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.DragonInvHandler;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.BetterWaterBoundPathNavigator;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.LessShitLookController;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.*;
import com.github.wolfshotz.wyrmroost.entities.util.EntityDataEntry;
import com.github.wolfshotz.wyrmroost.items.staff.StaffAction;
import com.github.wolfshotz.wyrmroost.network.packets.AnimationPacket;
import com.github.wolfshotz.wyrmroost.network.packets.KeybindPacket;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import com.github.wolfshotz.wyrmroost.util.TickFloat;
import com.github.wolfshotz.wyrmroost.util.animation.Animation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;
import net.neoforged.neoforge.common.NeoForgeMod;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class ButterflyLeviathanEntity extends AbstractDragonEntity
{
    public static final EntityDataAccessor<Boolean> HAS_CONDUIT = SynchedEntityData.defineId(ButterflyLeviathanEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> LIGHTNING_COOLDOWN = SynchedEntityData.defineId(ButterflyLeviathanEntity.class, EntityDataSerializers.INT);
    public static final Animation LIGHTNING_ANIMATION = new Animation(64);
    public static final Animation CONDUIT_ANIMATION = new Animation(59);
    public static final Animation BITE_ANIMATION = new Animation(17);
    public static final int CONDUIT_SLOT = 0;

    public final TickFloat beachedTimer = new TickFloat().setLimit(0, 1);
    public final TickFloat swimTimer = new TickFloat().setLimit(0, 1);
    public final TickFloat sitTimer = new TickFloat().setLimit(0, 1);
    public boolean beached = true;

    public ButterflyLeviathanEntity(EntityType<? extends AbstractDragonEntity> dragon, Level world)
    {
        super(dragon, world);
        noCulling = WRConfig.disableFrustumCheck;
        moveControl = new MoveController();
        setPathfindingMalus(PathType.WATER, 0);

        registerDataEntry("Variant", EntityDataEntry.INTEGER, VARIANT);
        registerDataEntry("LightningCooldown", EntityDataEntry.INTEGER, LIGHTNING_COOLDOWN);
    }

    @Override
    protected void registerGoals()
    {
        goalSelector.addGoal(0, new WRSitGoal(this));
        goalSelector.addGoal(1, new MoveToHomeGoal(this));
        goalSelector.addGoal(2, new AttackGoal());
        goalSelector.addGoal(3, new WRFollowOwnerGoal(this));

        goalSelector.addGoal(4, new DragonBreedGoal(this));
        goalSelector.addGoal(5, new JumpOutOfWaterGoal());
        goalSelector.addGoal(6, new RandomSwimmingGoal(this, 1, 40));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, LivingEntity.class, 14f));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        targetSelector.addGoal(0, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new HurtByTargetGoal(this));
        targetSelector.addGoal(4, new DefendHomeGoal(this));
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, false, e ->
        {
            EntityType<?> type = e.getType();
            return e.isInWater() == isInWater() && (type == EntityType.PLAYER || type == EntityType.GUARDIAN || type == EntityType.SQUID);
        }));
    }

    @Override
    protected void  defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_CONDUIT, false);
        builder.define(LIGHTNING_COOLDOWN, 0);
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        Vec3 conduitPos = getConduitPos();

        // cooldown for lightning attack
        if (getLightningCooldown() > 0)
            setLightningCooldown(getLightningCooldown() - 1);

        // handle "beached" logic (if this fat bastard is on land)
        boolean prevBeached = beached;
        if (!beached && onGround() && !wasTouchingWater) beached = true;
        else if (beached && wasTouchingWater) beached = false;
        if (prevBeached != beached) refreshDimensions();
        beachedTimer.add((beached)? 0.1f : -0.05f);
        swimTimer.add(isUnderWater()? -0.1f : 0.1f);
        sitTimer.add(isInSittingPose()? 0.1f : -0.1f);

        if (isJumpingOutOfWater())
        {
            Vec3 motion = getDeltaMovement();
            setXRot((float) (Math.signum(-motion.y) * Math.acos(Math.sqrt(motion.horizontalDistanceSqr()) / motion.length()) * (double) (180f / Mafs.PI)) * 0.725f);
        }

        // conduit effects
        if (hasConduit()) {
            if (level().isClientSide() && isInWaterRainOrBubble() && random.nextDouble() <= 0.1) {
                for (int i = 0; i < 16; ++i)
                    level().addParticle(ParticleTypes.NAUTILUS,
                            conduitPos.x,
                            conduitPos.y + 2.25,
                            conduitPos.z,
                            Mafs.nextDouble(random) * 1.5f,
                            Mafs.nextDouble(random),
                            Mafs.nextDouble(random) * 1.5f);
            }

            // nearby entities: if evil, kill, if not, give reallly cool potion effect
            if (tickCount % 80 == 0) {
                for (LivingEntity entity : getEntitiesNearby(25, Entity::isInWaterRainOrBubble)) {
                    if (entity instanceof Player || isAlliedTo(entity))
                        entity.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 220, 0, true, true));

                    if ((entity.getType().is(WREntities.Tags.BUTTERFLY_LEVIATHAN_CONDUIT_TARGETS) || entity == getTarget()) && canAttack(entity) && !isAlliedTo(entity)) {
                        entity.hurt(damageSources().magic(), 4);
                        playSound(SoundEvents.CONDUIT_ATTACK_TARGET, 1, 1);
                    }
                }
            }

            // play some sounds because immersion is important for some reason
            if (level().isClientSide() && tickCount % 100 == 0)
                if (random.nextBoolean()) playSound(SoundEvents.CONDUIT_AMBIENT, 1f, 1f, true);
                else playSound(SoundEvents.CONDUIT_AMBIENT_SHORT, 1f, 1f, true);
        }

        // handle animation logic
        Animation animation = getAnimation();
        int animTick = getAnimationTick();

        // zap the fuckers
        if (animation == LIGHTNING_ANIMATION) {
            setLightningCooldown(getLightningCooldown() + 6);
            if (animTick == 10) playSound(WRSounds.ENTITY_BFLY_ROAR.value(), 3f, 1f, true);
            if (!level().isClientSide() && isInWaterRainOrBubble() && animTick >= 10) {
                LivingEntity target = getTarget();
                if (target != null) {
                    if (hasConduit()) {
                        if (animTick % 10 == 0) {
                            Vec3 vec3d = target.position().add(Mafs.nextDouble(random) * 2.333, 0, Mafs.nextDouble(random) * 2.333);
                            createLightning(level(), vec3d, false);
                        }
                    } else if (animTick == 10) createLightning(level(), target.position(), false);
                }
            }
        }
        else if (animation == CONDUIT_ANIMATION) // oooo very scary
        {
            ((LessShitLookController) getLookControl()).restore();
            if (animTick == 0) playSound(WRSounds.ENTITY_BFLY_ROAR.value(), 5f, 1, true);
            else if (animTick == 15)
            {
                playSound(SoundEvents.BEACON_ACTIVATE, 1, 1);
                if (!level().isClientSide()) createLightning(level(), getConduitPos().add(0, 1, 0), true);
                else
                {
                    for (int i = 0; i < 26; ++i)
                    {
                        double velX = Math.cos(i);
                        double velZ = Math.sin(i);
                        level().addParticle(ParticleTypes.CLOUD, conduitPos.x, conduitPos.y + 0.8, conduitPos.z, velX, 0, velZ);
                    }
                }
            }
        }
        else if (animation == BITE_ANIMATION)
        {
            if (animTick == 0) playSound(WRSounds.ENTITY_BFLY_HURT.value(), 1, 1, true);
            else if (animTick == 6)
                attackInBox(getBoundingBox().move(Vec3.directionFromRotation(isInWater()? getXRot() : 0, yHeadRot).scale(5.5f)).inflate(0.85), 40);
        }
    }

    @Override
    public InteractionResult actuallyInteractWithMob(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (((beached && getLightningCooldown() > 60 && level().isRainingAt(blockPosition())) || player.isCreative() || isBaby()) && isFoodItem(stack))
        {
            eat(stack);
            if (!level().isClientSide()) tame(random.nextDouble() < 0.2, player);
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        return super.actuallyInteractWithMob(player, hand);
    }

    @Override
    public void travel(Vec3 vec3d)
    {
        if (isInWater())
        {
            if (hasControllingPassenger()) {
                float speed = getTravelSpeed() * 0.225f;
                LivingEntity entity = getControllingPassenger();
                double moveY = vec3d.y;
                double moveX = vec3d.x;
                double moveZ = entity.zza;

                yHeadRot = entity.yHeadRot;
                if (!isJumpingOutOfWater()) setXRot(entity.getXRot() * 0.5f);
                double lookY = entity.getLookAngle().y;
                if (entity.zza != 0 && (isUnderWater() || lookY < 0)) moveY = lookY;

                setSpeed(speed);
                vec3d = new Vec3(moveX, moveY, moveZ);
            }

            // add motion if were coming out of water fast; jump out of water like a dolphin
            if (getDeltaMovement().y > 0.25 && level().getBlockState(new BlockPos(getBlockX(), (int) getEyeY(), getBlockZ()).above()).getFluidState().isEmpty())
                setDeltaMovement(getDeltaMovement().multiply(1.2, 1.5f, 1.2d));

            moveRelative(getSpeed(), vec3d);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.9d));

            double xDiff = getX() - xo;
            double yDiff = getY() - yo;
            double zDiff = getZ() - zo;
            if (yDiff < 0.2) yDiff = 0;
            float amount = (float) (Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff) * 4f);
            if (amount > 1f) amount = 1f;

            walkAnimation.update(amount, 0.4f);

            if (vec3d.z == 0 && getTarget() == null && !isInSittingPose())
                setDeltaMovement(getDeltaMovement().add(0, -0.003d, 0));
        }
        else super.travel(vec3d);
    }

    @Override
    public float getTravelSpeed()
    {
        //@formatter:off
        return isInWater()? (float) getAttributeValue(NeoForgeMod.SWIM_SPEED)
                          : (float) getAttributeValue(Attributes.MOVEMENT_SPEED);
        //@formatter:on
    }

    @Override
    public ItemStack eat(Level world, ItemStack stack, FoodProperties foodProperties) {
        setLightningCooldown(0);
        return super.eat(world, stack, foodProperties);
    }

    @Override
    public void doSpecialEffects()
    {
        if (getVariant() == -1 && tickCount % 25 == 0)
        {
            double x = getX() + (Mafs.nextDouble(random) * getBbWidth() + 1);
            double y = getY() + (random.nextDouble() * getBbHeight() + 1);
            double z = getZ() + (Mafs.nextDouble(random) * getBbWidth() + 1);
            level().addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0.05f, 0);
        }
    }

    @Override
    public void onInvContentsChanged(int slot, ItemStack stack, boolean onLoad)
    {
        if (slot == CONDUIT_SLOT)
        {
            boolean flag = stack.getItem() == Items.CONDUIT;
            boolean hadConduit = hasConduit();
            entityData.set(HAS_CONDUIT, flag);
            if (!onLoad && flag && !hadConduit) setAnimation(CONDUIT_ANIMATION);
        }
    }

    @Override
    public void recievePassengerKeybind(int key, int mods, boolean pressed)
    {
        if (pressed && noActiveAnimation())
        {
            if (key == KeybindPacket.MOUNT_KEY1) setAnimation(BITE_ANIMATION);
            else if (key == KeybindPacket.MOUNT_KEY2 && !level().isClientSide() && canZap())
            {
                EntityHitResult ertr = Mafs.rayTraceEntities(getControllingPlayer(), 40, e -> e instanceof LivingEntity && e != this);
                if (ertr != null && wantsToAttack((LivingEntity) ertr.getEntity(), getOwner()))
                {
                    setTarget((LivingEntity) ertr.getEntity());
                    AnimationPacket.send(this, LIGHTNING_ANIMATION);
                }
            }
        }
    }

    public Vec3 getConduitPos()
    {
        return getEyePosition(1)
                .add(0, 0.4, 0)
                .add(calculateViewVector(getXRot(), getYRot()).multiply(4d, -4d, 4));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addScreenInfo(StaffScreen screen)
    {
        screen.addAction(StaffAction.INVENTORY);
        screen.addAction(StaffAction.TARGET);
        super.addScreenInfo(screen);
    }

    @Override
    public void addContainerInfo(DragonInvContainer container)
    {
        super.addContainerInfo(container);
        container.addSlot(new SlotBuilder(getInvHandler(), CONDUIT_SLOT).only(Items.CONDUIT).limit(1));
    }

    @Override
    public void setMountCameraAngles(boolean backView, CalculateDetachedCameraDistanceEvent event)
    {
        if (backView) event.getCamera().move(-10f, 1, 0);
        else event.getCamera().move(-5, -0.75f, 0);
    }

    @Override
    public int getMaxSpawnClusterSize()
    {
        return 1;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader world)
    {
        return world.isUnobstructed(this);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean isFoodItem(ItemStack stack) {
        return stack.is(WRItems.Tags.BUTTERFLY_LEVIATHAN_FOOD);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.is(WRItems.Tags.BUTTERFLY_LEVIATHAN_BREEDING_ITEMS);
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
        return WRSounds.ENTITY_BFLY_IDLE.value();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_BFLY_HURT.value();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_BFLY_DEATH.value();
    }

    protected BetterWaterBoundPathNavigator createNavigation(Level world)
    {
        return new BetterWaterBoundPathNavigator(this, level());
    }

    public boolean hasConduit()
    {
        return entityData.get(HAS_CONDUIT);
    }

    @Override
    public DragonInvHandler createInv()
    {
        return new DragonInvHandler(this, 1);
    }

    public boolean isJumpingOutOfWater()
    {
        return !isInWater() && !beached;
    }

    public boolean canZap() {
        return isInWaterRainOrBubble() && getLightningCooldown() <= 0;
    }

    @Override
    public boolean isImmuneToArrows()
    {
        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return ModUtils.equalsAny(source, damageSources().lightningBolt(), damageSources().inFire(), damageSources().inWall()) || super.isInvulnerableTo(source);
    }

    @Override
    public EntityDimensions getDimensions(Pose poseIn) {
        return getType().getDimensions().scale(getScale()).withEyeHeight(getBbHeight() * (beached? 1f : 0.6f));
    }

    @Override
    protected boolean canRide(Entity entityIn)
    {
        return isTame() && !isBaby();
    }

    @Override // 2 passengers
    protected boolean canAddPassenger(Entity passenger)
    {
        return getPassengers().size() < 2;
    }

    @Override
    public Vec3 getPassengerPosOffset(Entity entity, int index)
    {
        return new Vec3(0, -0.6, index == 1? -2 : 0);
    }

    @Override
    public int getYawRotationSpeed()
    {
        return 6;
    }

    @Override
    public int determineVariant()
    {
        return random.nextDouble() < 0.02? -1 : random.nextInt(2);
    }

    @Override
    public boolean canFly()
    {
        return false;
    }

    @Override
    public Animation[] getAnimations()
    {
        return new Animation[] {LIGHTNING_ANIMATION, CONDUIT_ANIMATION, BITE_ANIMATION};
    }

    @Override
    public boolean hasGender() {
        return false;
    }

    @Override
    public boolean mayFly() {
        return false;
    }

    @Override
    public boolean maySleep() {
        return false;
    }

    @Override
    public boolean hasVariants() {
        return true;
    }

    private static void createLightning(Level world, Vec3 position, boolean effectOnly)
    {
        if (world.isClientSide()) return;
        LightningBolt entity = EntityType.LIGHTNING_BOLT.create(world);
        entity.moveTo(position);
        entity.setVisualOnly(effectOnly);
        world.addFreshEntity(entity);
    }

    public static <F extends Mob> boolean getSpawnPlacement(EntityType<F> fEntityType, ServerLevelAccessor world, MobSpawnType reason, BlockPos pos, RandomSource random)
    {
        if (reason == MobSpawnType.SPAWNER) return true;
        return world.getFluidState(pos).is(FluidTags.WATER) && random.nextFloat() < 0.35f;
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return AbstractDragonEntity.createDragonAttributes()
                .add(Attributes.MAX_HEALTH, 180)
                .add(Attributes.MOVEMENT_SPEED, 0.08)
                .add(NeoForgeMod.SWIM_SPEED, 0.3)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .add(Attributes.ATTACK_DAMAGE, 14)
                .add(Attributes.STEP_HEIGHT, 2)
                .add(Attributes.FOLLOW_RANGE, 50);
    }

    public void setLightningCooldown(int cooldown) {
        entityData.set(LIGHTNING_COOLDOWN, cooldown);
    }

    public int getLightningCooldown() {
        return entityData.get(LIGHTNING_COOLDOWN);
    }

    @Override
    public boolean canRestUnderWater() {
        return true;
    }

    private class MoveController extends MoveControl
    {
        public MoveController()
        {
            super(ButterflyLeviathanEntity.this);
        }

        public void tick()
        {
            if (operation == Operation.MOVE_TO && !hasControllingPassenger()) {
                operation = Operation.WAIT;
                double x = wantedX - getX();
                double y = wantedY - getY();
                double z = wantedZ - getZ();
                double distSq = x * x + y * y + z * z;
                if (distSq < 2.5000003E-7) setZza(0f); // why move...
                else {
                    float yaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90f;
                    float pitch = -((float) (Math.atan2(y, Math.sqrt(x * x + z * z)) * 180 / Math.PI));
                    pitch = Mth.clamp(Mth.wrapDegrees(pitch), -85f, 85f);

                    yHeadRot = yaw;
                    setYRot(yBodyRot  = rotlerp(getYRot(), yHeadRot, getYawRotationSpeed()));
                    setXRot(rotlerp(getXRot(), pitch, 75));
                    ((LessShitLookController) getLookControl()).freeze();
                    float speed = isInWater()? (float) getAttributeValue(NeoForgeMod.SWIM_SPEED) : (float) getAttributeValue(Attributes.MOVEMENT_SPEED);
                    setSpeed(speed);
                    if (isInWater())
                    {
                        zza = Mth.cos(pitch * (Mafs.PI / 180f)) * speed;
                        yya = -Mth.sin(pitch * (Mafs.PI / 180f)) * speed;
                    }
                }
            }
            else {
                setSpeed(0);
                setXxa(0);
                setYya(0);
                setZza(0);
            }
        }
    }

    private class AttackGoal extends Goal {
        public AttackGoal()
        {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return !hasControllingPassenger() && getTarget() != null;
        }

        @Override
        public void tick() {
            LivingEntity target = getTarget();
            if (target == null) return;
            double distFromTarget = distanceToSqr(target);

            getLookControl().setLookAt(target, getMaxHeadYRot(), getMaxHeadXRot());

            boolean isClose = distFromTarget < 40;

            getNavigation().moveTo(target, 1.2);

            if (isClose) setYRot((float) Mafs.getAngle(ButterflyLeviathanEntity.this, target) + 90f);

            if (noActiveAnimation()) {
                if (!isClose && (isTame() || target.getType() == EntityType.PLAYER) && canZap())
                    AnimationPacket.send(ButterflyLeviathanEntity.this, LIGHTNING_ANIMATION);
                else if (isClose && Mth.degreesDifferenceAbs((float) Mafs.getAngle(ButterflyLeviathanEntity.this, target) + 90, getYRot()) < 30)
                    AnimationPacket.send(ButterflyLeviathanEntity.this, BITE_ANIMATION);
            }
        }
    }

    private class JumpOutOfWaterGoal extends Goal
    {
        private BlockPos pos;

        public JumpOutOfWaterGoal()
        {
            setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override
        public boolean canUse()
        {
            if (isInSittingPose()) return false;
            if (hasControllingPassenger()) return false;
            if (!isUnderWater()) return false;
            if (level().getFluidState(this.pos = level().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, blockPosition()).below()).isEmpty())
                return false;
            if (pos.getY() <= 0) return false;
            return random.nextDouble() < 0.001;
        }

        @Override
        public boolean canContinueToUse() {
            return !hasControllingPassenger() && isUnderWater();
        }

        @Override
        public void start() {
            getNavigation().stop();
            this.pos = pos.relative(getDirection(), (int) ((pos.getY() - getY()) * 0.5d));
        }

        @Override
        public void tick()
        {
            getMoveControl().setWantedPosition(pos.getX(), pos.getY(), pos.getZ(), 1.2d);
        }

        @Override
        public void stop() {
            pos = null;
            stopInPlace();
        }
    }
}
