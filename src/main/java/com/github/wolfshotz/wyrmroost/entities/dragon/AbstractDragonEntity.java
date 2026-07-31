package com.github.wolfshotz.wyrmroost.entities.dragon;

import com.github.wolfshotz.wyrmroost.WRConfig;
import com.github.wolfshotz.wyrmroost.attachments.ShoulderDragon;
import com.github.wolfshotz.wyrmroost.client.render.RenderHelper;
import com.github.wolfshotz.wyrmroost.client.screen.StaffScreen;
import com.github.wolfshotz.wyrmroost.client.sounds.FlyingSound;
import com.github.wolfshotz.wyrmroost.containers.DragonInvContainer;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.DragonInvHandler;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.*;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.WRSitGoal;
import com.github.wolfshotz.wyrmroost.entities.dragonegg.DragonEggProperties;
import com.github.wolfshotz.wyrmroost.entities.util.EntityDataEntry;
import com.github.wolfshotz.wyrmroost.items.DragonArmorItem;
import com.github.wolfshotz.wyrmroost.items.DragonEggItem;
import com.github.wolfshotz.wyrmroost.items.staff.StaffAction;
import com.github.wolfshotz.wyrmroost.registry.WRAttachments;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import com.github.wolfshotz.wyrmroost.util.TickFloat;
import com.github.wolfshotz.wyrmroost.util.animation.Animation;
import com.github.wolfshotz.wyrmroost.util.animation.IAnimatable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by com.github.WolfShotz 7/10/19 - 21:36
 * This is where the magic happens. Here be our Dragons!
 */
public abstract class AbstractDragonEntity extends TamableAnimal implements IAnimatable, MenuProvider {
    public static final byte HEAL_PARTICLES_DATA_ID = 8;
    public static final int MAX_SHOULDER_DRAGON_PER_PLAYER_COUNT = 3;

    // Common Data Parameters
    public static final EntityDataAccessor<Boolean> GENDER = SynchedEntityData.defineId(AbstractDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(AbstractDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(AbstractDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(AbstractDragonEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Optional<BlockPos>> HOME_POS = SynchedEntityData.defineId(AbstractDragonEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);

    private final Set<EntityDataEntry<?>> dataEntries = new HashSet<>();
    @Nullable
    public final DragonInvHandler invHandler;
    public final TickFloat sleepTimer = new TickFloat().setLimit(0, 1);
    private int sleepCooldown;
    public boolean wingsDown;
    public int breedCount;
    private Animation animation = NO_ANIMATION;
    private int animationTick;

    public AbstractDragonEntity(EntityType<? extends AbstractDragonEntity> dragon, Level world)
    {
        super(dragon, world);

        invHandler = createInv();
        lookControl = new LessShitLookController(this);
        if (mayFly()) moveControl = new FlyerMoveController(this);

        registerDataEntry("HomePos", EntityDataEntry.BLOCK_POS.optional(), HOME_POS);
        registerDataEntry("BreedCount", EntityDataEntry.INTEGER, () -> breedCount, i -> breedCount = i);
        if (invHandler != null) {
            registerDataEntry(
                    "Inv", EntityDataEntry.COMPOUND,
                    () -> invHandler.serializeNBT(level().registryAccess()),
                    (tag) -> invHandler.deserializeNBT(level().registryAccess(), tag)
            );
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FLYING, false);
        builder.define(VARIANT, 0);
        builder.define(SLEEPING, false);
        builder.define(GENDER, false);
        builder.define(HOME_POS, Optional.empty());
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn)
    {
        return new BetterPathNavigator(this);
    }

    @Override
    protected BodyRotationControl createBodyControl()
    {
        return new DragonBodyController(this);
    }

    @Override
    protected void registerGoals()
    {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new WRSitGoal(this));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt)
    {
        super.addAdditionalSaveData(nbt);
        for (EntityDataEntry<?> entry : dataEntries) entry.write(nbt);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt)
    {
        super.readAdditionalSaveData(nbt);
        for (EntityDataEntry<?> entry : dataEntries) entry.read(nbt);
        applyAttributes();
    }

    public <T> void registerDataEntry(String key, EntityDataEntry.SerializerType<T> type, Supplier<T> write, Consumer<T> read)
    {
        if (!level().isClientSide()) dataEntries.add(new EntityDataEntry<>(key, type, write, read));
    }

    public <T> void registerDataEntry(String key, EntityDataEntry.SerializerType<T> type, EntityDataAccessor<T> param) {
        registerDataEntry(key, type, () -> entityData.get(param), v -> entityData.set(param, v));
    }

    public int getVariant()
    {
        return hasVariants() ? entityData.get(VARIANT) : 0;
    }

    public void setVariant(int variant)
    {
        entityData.set(VARIANT, variant);
    }

    /**
     * @return true for male, false for female. anything else is a political abomination and needs to be cancelled.
     */
    public boolean isMale()
    {
        return hasGender() ? entityData.get(GENDER) : true;
    }

    public void setGender(boolean sex)
    {
        entityData.set(GENDER, sex);
    }

    public boolean isSleeping()
    {
        return maySleep() ? entityData.get(SLEEPING) : false;
    }

    public void setSleeping(boolean sleep)
    {
        if (isSleeping() == sleep) return;

        entityData.set(SLEEPING, sleep);
        if (!level().isClientSide())
        {
            if (sleep) stopInPlace();
            else sleepCooldown = 350;
        }
    }

    public boolean shouldSleep()
    {
        if (sleepCooldown > 0) return false;
        if (level().isDay()) return false;
        if (!isIdling()) return false;
        if (isTame())
        {
            if (isAtHome())
            {
                if (defendsHome()) return getHealth() < getMaxHealth() * 0.25;
            }
            else if (!isInSittingPose()) return false;
        }

        return random.nextDouble() < 0.0065;
    }

    public boolean shouldWakeUp()
    {
        return level().isDay() && random.nextDouble() < 0.0065;
    }

    public boolean isFlying() {
        return entityData.get(FLYING);
    }

    public void setFlying(boolean fly)
    {
        if (isFlying() == fly) return;
        entityData.set(FLYING, fly);
        if (fly) {
            // make sure NOT to switch the navigator if liftoff fails
            if (liftOff()) navigation = new FlyerPathNavigator(this);
        }
        else navigation = new BetterPathNavigator(this);
    }

    public boolean hasArmor() {
        return (getBodyArmorItem().getItem() instanceof DragonArmorItem);
    }

    public ItemStack getArmor() {
        return hasArmor()? getBodyArmorItem() : ItemStack.EMPTY;
    }

    public void setArmor(@Nullable ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof DragonArmorItem)) stack = ItemStack.EMPTY;
        setBodyArmorItem(stack);
    }

    public void setSit(boolean sitting)
    {
        setOrderedToSit(sitting);
        setInSittingPose(sitting);
    }

    @Override
    public void setInSittingPose(boolean sitting)
    {
        super.setInSittingPose(sitting);
        if (sitting) stopInPlace();
    }

    public DragonInvHandler getInvHandler()
    {
        if (invHandler == null) throw new NoSuchElementException("This boi doesn't have an inventory wtf are u doing");
        return invHandler;
    }

    public DragonInvHandler createInv()
    {
        return null;
    }

    @Override
    public void tick()
    {
        super.tick();
        updateAnimations();
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        if (isEffectiveAi()) {
            // uhh so were falling, we should probably start flying
            //boolean flying = shouldFly();
            //if (flying != isFlying()) setFlying(flying);

            //removing thing above got some consequences for AI, thus let's make thing that would emulate it
            if (!isTame() && !isFlying()) {
                if (shouldFly()) setFlying(true);
            }

            if (onGround()) setFlying(false);

            if (sleepCooldown > 0) --sleepCooldown;
            if (isSleeping()) {
                ((LessShitLookController) getLookControl()).restore();
                if (getHealth() < getMaxHealth() && random.nextDouble() < 0.005) heal(1);
                if (shouldWakeUp()) setSleeping(false);
            } else if (shouldSleep()) setSleeping(true);


            // todo figure out a better target system?
            LivingEntity target = getTarget();
            if (target != null && (!target.isAlive() || !canAttack(target) || !wantsToAttack(target, getOwner())))
                setTarget(null);
        }
        else doSpecialEffects();
    }

    /**
     * Not to be confused with {@link #positionRider(Entity)}, as this is called when were riding something
     */
    @Override
    public void rideTick() {
        super.rideTick();

        Entity entity = getVehicle();

        if (entity == null || !entity.isAlive()) {
            stopRiding();
            return;
        }

        setDeltaMovement(Vec3.ZERO);
        stopInPlace();

        if (entity instanceof Player) {
            Player player = (Player) entity;

            int index = player.getPassengers().indexOf(this);
            if ((player.isShiftKeyDown() && !player.getAbilities().flying) || isInWater() || index > 2)
            {
                stopRiding();
                setSit(false);
                return;
            }

            setXRot(xRotO = player.getXRot() / 2);
            setYRot(yHeadRot = yBodyRot = yRotO = player.getYRot());
            setRot(player.yHeadRot, getXRot());

            Vec3 vec3d = getRidingPosOffset(index);
            Vec3 pos = Mafs.getYawVec(player.yBodyRot, vec3d.x, vec3d.z).add(player.getX(), player.getY() + vec3d.y, player.getZ());
            setPos(pos.x, pos.y, pos.z);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public Vec3 getRidingPosOffset(int passengerIndex) {
        double x = getBbWidth() * 0.5d + getVehicle().getBbWidth() * 0.5d;
        Entity vehicle = getVehicle();
        switch (passengerIndex) {
            default:
            case 0:
                return new Vec3(0, vehicle instanceof Player player ? player.getBbHeight() : 1.81, 0);
            case 1:
                return new Vec3(x, vehicle instanceof Player player ? player.getEyeHeight() : 1.38d, 0);
            case 2:
                return new Vec3(-x, vehicle instanceof Player player ? player.getEyeHeight() :  1.38d, 0);
        }
    }

    /**
     * Not to be confused with {@link #rideTick()}, as this is called when were being ridden by something
     */
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float delta) {
        int i = Math.max(this.getPassengers().indexOf(entity), 0);
        return super.getPassengerAttachmentPoint(entity, dimensions, delta).add(getPassengerPosOffset(entity, i).yRot(-yBodyRot * Mth.DEG_TO_RAD));
    }

    public Vec3 getPassengerPosOffset(Entity entity, int index) {
        return new Vec3(0, 0, 0);
    }

    // Ok so some basic notes here:
    // if the action result is a SUCCESS, the player swings its arm.
    // however, itll send that arm swing twice if we aren't careful.
    // essentially, returning SUCCESS on server will send a swing arm packet to notify the client to animate the arm swing
    // client tho, it will just animate it.
    // so if we aren't careful, both will happen. So its important to do the following for common execution:
    // ActionResultType.func_233537_a_(World::isRemote)
    // essentially, if the provided boolean is true, it will return SUCCESS, else CONSUME.
    // so since the world is client, it will be SUCCESS on client and CONSUME on server.
    // That way, the server never sends the arm swing packet.
    public InteractionResult actuallyInteractWithMob(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (isOwnedBy(player) && player.isShiftKeyDown() && !isFlying()) {
            setSit(!isInSittingPose());
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        if (isTame()) {
            if (isFoodItem(stack)) {
                boolean flag = getHealth() < getMaxHealth();
                if (isBaby()) {
                    if (!level().isClientSide()) ageUp((int) ((-getAge() / 20) * 0.1F), true);
                    flag = true;
                }
                if (flag) {
                    eat(stack);
                    return InteractionResult.sidedSuccess(level().isClientSide());
                }
            }

            if (isBreedingItem(stack) && getAge() == 0) {
                if (!level().isClientSide() && !isInLove()) {
                    eat(stack);
                    setInLove(player);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.CONSUME;
            }
        }

        if (canRide(player) && !player.isShiftKeyDown()) {
            if (!level().isClientSide()) {
                player.startRiding(this);
                stopInPlace();
            }
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        if (canRidePlayers() && isOwnedBy(player) && player.getPassengers().size() < MAX_SHOULDER_DRAGON_PER_PLAYER_COUNT && !player.isShiftKeyDown() && !isLeashed()) {
            setSit(true);
            setFlying(false);
            stopInPlace();
            startRiding(player, true);
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean startRiding(Entity entity, boolean force) {
        boolean result = super.startRiding(entity, force);
        if (canRidePlayers() && result && entity instanceof Player player) {
            CompoundTag nbtCompound = new CompoundTag();
            saveAsPassenger(nbtCompound);
            List<ShoulderDragon> shoulderDragonList = new ArrayList<>(player.getData(WRAttachments.SHOULDER_DRAGON_LIST));
            boolean alreadyIn = shoulderDragonList.stream().anyMatch(d -> {
                UUID uuid = d.nbt().getUUID("UUID");
                return getUUID().equals(uuid);
            });
            if (!alreadyIn) {
                shoulderDragonList.add(new ShoulderDragon(getVehicle().getPassengers().indexOf(this), nbtCompound));
                player.setData(WRAttachments.SHOULDER_DRAGON_LIST, shoulderDragonList);
            }
            setPortalCooldown(0);
        }
        return result;
    }

    @Override
    public void stopRiding() {
        if (getVehicle() instanceof Player player) {
            if (player instanceof ServerPlayer serverPlayer && serverPlayer.hasDisconnected()) return;
            List<ShoulderDragon> shoulderDragonList = new ArrayList<>(player.getData(WRAttachments.SHOULDER_DRAGON_LIST));
            shoulderDragonList.removeIf(d -> {
                UUID uuid = d.nbt().getUUID("UUID");
                return getUUID().equals(uuid);
            });
            player.setData(WRAttachments.SHOULDER_DRAGON_LIST, shoulderDragonList);
            setYRot(player.getYRot() + 180f);
        }
        super.stopRiding();
    }

    // Override to make processInteract way less annoying
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        InteractionResult result = stack.interactLivingEntity(player, this, hand);
        if (!result.consumesAction()) result = actuallyInteractWithMob(player, hand);
        if (result.consumesAction()) setSleeping(false);
        return result;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void travel(Vec3 vec3d) {
        float speed = getTravelSpeed();

        if (hasControllingPassenger()) { // Were being controlled; override ai movement
            LivingEntity entity = getControllingPassenger();
            double moveY = vec3d.y;
            double moveX = entity.xxa * 0.5;
            double moveZ = entity.zza;

            // rotate head to match driver. rotationYaw is handled relative to this.
            yHeadRot = entity.yHeadRot;
            setXRot(entity.getXRot() * 0.5f);

            if (isFlying())
            {
                if (entity.zza != 0) moveY = entity.getLookAngle().y * speed * 18;
                moveX = vec3d.x;

                if (entity instanceof ServerPlayer player)
                    player.connection.clientVehicleIsFloating = false;
            }
            else
            {
                speed *= 0.35f;
                if (shouldFly() || entity.jumping && canFly()) setFlying(true);
            }

            setSpeed(speed);
            vec3d = new Vec3(moveX, moveY, moveZ);
        }

        if (isFlying()) {
            // Move relative to rotationYaw - handled in the move controller or by the passenger
            moveRelative(speed, vec3d);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.88f));

            // hover in place
            Vec3 motion = getDeltaMovement();
            if (motion.length() < 0.04f) setDeltaMovement(motion.add(0, Math.cos(tickCount * 0.1f) * 0.02f, 0));

            // limb swinging animations
            float limbSpeed = 0.4f;
            float amount = 1f;
            if (getY() - yo < -0.1f)
            {
                amount = 0f;
                limbSpeed = 0.2f;
            }

            walkAnimation.update(amount, limbSpeed);
            return;
        }

        super.travel(vec3d.add(0, -0.001, 0));
    }

    public float getTravelSpeed()
    {
        //@formatter:off
        return isFlying()? (float) getAttributeValue(Attributes.FLYING_SPEED)
                         : (float) getAttributeValue(Attributes.MOVEMENT_SPEED);
        //@formatter:on
    }

    public boolean shouldFly() {
        boolean ignoreAltitude = getY() <= level().getMinBuildHeight() + 1;
        return canFly() && !onGround() && (ignoreAltitude || getAltitude() > getFlightThreshold());
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onSyncedDataUpdated(EntityDataAccessor<?> key)
    {
        if (key.equals(SLEEPING) || key.equals(FLYING) || key.equals(DATA_FLAGS_ID))
        {
            refreshDimensions();
            if (level().isClientSide() && key == FLYING && isFlying() && hasControllingPassenger())
                FlyingSound.play(this);
        }
        if (key.equals(FLYING)) {

        }
        else super.onSyncedDataUpdated(key);

    }

    @Override
    public void handleEntityEvent(byte id)
    {
        if (id == HEAL_PARTICLES_DATA_ID)
        {
            for (int i = 0; i < getBbWidth() * getBbHeight(); ++i)
            {
                double x = getX() + Mafs.nextDouble(random) * getBbWidth() + 0.4d;
                double y = getY() + random.nextDouble() * getBbHeight();
                double z = getZ() + Mafs.nextDouble(random) * getBbWidth() + 0.4d;
                level().addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0, 0, 0);
            }
        }
        else super.handleEntityEvent(id);
    }

    public ItemStack getStackInSlot(int slot)
    {
        return invHandler != null ? invHandler.getStackInSlot(slot) : ItemStack.EMPTY;
    }

    /**
     * It is VERY important to be careful when using this.
     * It is VERY sidedness sensitive. If not done correctly, it can result in the loss of items! <P>
     * {@code if (!world.isReomote) setStackInSlot(...)}
     */
    public void setStackInSlot(int slot, ItemStack stack)
    {
        if (invHandler != null) invHandler.setStackInSlot(slot, stack);
    }

    public void attackInBox(AABB box)
    {
        attackInBox(box, 0);
    }

    public void attackInBox(AABB box, int disabledShieldTime)
    {
        List<LivingEntity> attackables = level().getEntitiesOfClass(LivingEntity.class, box, entity -> entity != this && !hasPassenger(entity) && wantsToAttack(entity, getOwner()));
        if (WRConfig.debugMode && level().isClientSide()) RenderHelper.DebugBox.INSTANCE.queue(box);
        for (LivingEntity attacking : attackables)
        {
            doHurtTarget(attacking);
            if (disabledShieldTime > 0 && attacking instanceof Player)
            {
                Player player = ((Player) attacking);
                if (player.isUsingItem() && player.getUseItem().getItem() instanceof ShieldItem item)
                {
                    player.getCooldowns().addCooldown(item, disabledShieldTime);
                    player.stopUsingItem();
                    level().broadcastEntityEvent(player, (byte) 9);
                }
            }
        }
    }

    public AABB getOffsetBox(float offset)
    {
        return getBoundingBox().move(Vec3.directionFromRotation(0, yBodyRot).scale(offset));
    }

    @Override // Dont damage owners other pets!
    public boolean doHurtTarget(Entity entity)
    {
        if (isAlliedTo(entity)) return false;
        return super.doHurtTarget(entity);
    }

    @Override // We shouldnt be targetting pets...
    public boolean wantsToAttack(LivingEntity target, @Nullable LivingEntity owner)
    {
        return !isAlliedTo(target);
    }

    @Override
    public boolean canAttack(LivingEntity target)
    {
        return !isBaby() && !hasControllingPassenger() && super.canAttack(target);
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (isImmuneToArrows() && source.getDirectEntity() != null)
        {
            EntityType<?> attackSource = source.getDirectEntity().getType();
            if (attackSource == EntityType.ARROW) return false;
            else if (attackSource.is(WREntities.Tags.GEODE_ARROWS)) amount *= 0.5f;
        }

        setSleeping(false);
        setSit(false);
        return super.hurt(source, amount);
    }

    public void doSpecialEffects()
    {
    }

    public boolean tryTeleportToOwner()
    {
        if (getOwner() == null) return false;
        final int CONSTRAINT = (int) (getBbWidth() * 0.5) + 1;
        BlockPos pos = getOwner().blockPosition();
        BlockPos.MutableBlockPos potentialPos = new BlockPos.MutableBlockPos();

        for (int x = -CONSTRAINT; x < CONSTRAINT; x++)
            for (int y = 0; y < 4; y++)
                for (int z = -CONSTRAINT; z < CONSTRAINT; z++)
                {
                    potentialPos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (trySafeTeleport(potentialPos)) return true;
                }
        return false;
    }

    public boolean trySafeTeleport(BlockPos pos)
    {
        if (level().noCollision(this, getBoundingBox().move(pos.subtract(blockPosition()))))
        {
            moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, getYRot(), getXRot());
            return true;
        }
        return false;
    }

    @Override
    public BlockPos getRestrictCenter() {
        return getHomePos().orElse(super.getRestrictCenter());
    }

    public Optional<BlockPos> getHomePos() {
        return entityData.get(HOME_POS);
    }

    public void setHomePos(@Nullable BlockPos pos)
    {
        setHomePos(Optional.ofNullable(pos));
    }

    public void setHomePos(Optional<BlockPos> pos)
    {
        entityData.set(HOME_POS, pos);
    }

    public void clearHome()
    {
        setHomePos(Optional.empty());
    }

    @Override
    public boolean hasRestriction()
    {
        return getHomePos().isPresent();
    }

    @Override
    public float getRestrictRadius()
    {
        return WRConfig.homeRadius * WRConfig.homeRadius;
    }

    @Override
    public void restrictTo(BlockPos pos, int radius) {
        super.restrictTo(pos, radius);
        setHomePos(pos);
    }

    @Override
    public boolean isWithinRestriction(BlockPos pos) {
        Optional<BlockPos> home = getHomePos();
        return home.map(h -> h.distSqr(pos) <= getRestrictRadius()).orElse(true) || super.isWithinRestriction(pos);
    }

    public boolean isAtHome()
    {
        return hasRestriction() && isWithinRestriction();
    }

    @Override
    protected void dropEquipment()
    {
        if (invHandler != null) invHandler.getStacks().forEach(this::spawnAtLocation);
    }

    public void setRot(float yaw, float pitch)
    {
        setYRot(yaw % 360.0F);
        setXRot(pitch % 360.0F);
    }

    public double getAltitude()
    {
        BlockPos.MutableBlockPos pos = blockPosition().mutable();

        // cap to the world void (y = 0)
        while (pos.getY() >= level().getMinBuildHeight() && !level().getBlockState(pos.below()).isSolid()) pos.move(0, -1, 0);
        return getY() - pos.getY();
    }

    // overload because... WHY IS `World` A PARAMETER WTF THE FIELD IS LITERALLY PUBLIC
    public void eat(ItemStack stack)
    {
        FoodProperties foodProperties = stack.getFoodProperties(this);
        eat(level(), stack, foodProperties);
    }

    @Override
    public void elasticRangeLeashBehaviour(Entity p_353036_, float p_353047_) {
        super.elasticRangeLeashBehaviour(p_353036_, p_353047_);
        if (canFly() && !onGround()) setFlying(true);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public ItemStack eat(Level world, ItemStack stack, @Nullable FoodProperties foodProperties) {
        Vec3 mouth = getApproximateMouthPos();

        if (level().isClientSide())
        {
            double width = getBbWidth();
            for (int i = 0; i < Math.max(width * width * 2, 12); ++i)
            {
                Vec3 vec3d1 = new Vec3(((double) random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, ((double) random.nextFloat() - 0.5D) * 0.1D);
                vec3d1 = vec3d1.xRot(-getXRot() * (Mafs.PI / 180f));
                vec3d1 = vec3d1.yRot(-getYRot() * (Mafs.PI / 180f));
                world.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), mouth.x + Mafs.nextDouble(random) * (width * 0.2), mouth.y, mouth.z + Mafs.nextDouble(random) * (width * 0.2), vec3d1.x, vec3d1.y, vec3d1.z);
            }
            world.playSound(null, getX(), getY(), getZ(), getEatingSound(stack), SoundSource.NEUTRAL, 1f, 1f + (random.nextFloat() - random.nextFloat()) * 0.4f);
        }
        else
        {
            final float max = getMaxHealth();
            if (getHealth() < max) heal(Math.max((int) max / 5, 4)); // Base healing on max health, minumum 2 hearts.

            Item item = stack.getItem();
            if (foodProperties != null) addEatEffect(foodProperties);
            if (item.hasCraftingRemainingItem(stack))
                spawnAtLocation(item.getCraftingRemainingItem(stack), (float) (mouth.y - getY()));
            stack.shrink(1);
        }

        return stack;
    }

    public boolean tame(boolean tame, @Nullable Player tamer)
    {
        if (isTame()) return true;
        if (level().isClientSide()) return false;
        if (tame && tamer != null && !EventHooks.onAnimalTame(this, tamer))
        {
            tame(tamer);
            setHealth(getMaxHealth());
            stopInPlace();
            level().broadcastEntityEvent(this, (byte) 7); // heart particles
            return true;
        }
        else level().broadcastEntityEvent(this, (byte) 6); // black particles

        return false;
    }

    @Override
    public void heal(float healAmount)
    {
        super.heal(healAmount);
        level().broadcastEntityEvent(this, HEAL_PARTICLES_DATA_ID);
    }

    public int getYawRotationSpeed()
    {
        return isFlying()? 6 : 75;
    }

    public boolean isRiding()
    {
        return getVehicle() != null;
    }

    @Override
    public boolean canMate(Animal mate)
    {
        AbstractDragonEntity dragon = (AbstractDragonEntity) mate;
        if (isInSittingPose() || dragon.isInSittingPose()) return false;
        if (hasGender() && isMale() == dragon.isMale()) return false;
        return super.canMate(mate);
    }

    @Override
    public void setBaby(boolean child)
    {
        setAge(child? DragonEggProperties.get(getType()).getGrowthTime() : 0);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob mate)
    {
        return (AgeableMob) getType().create(level());
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel world, Animal mate)
    {
        final BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(this, mate, null);
        if (event.isCanceled()) // cancelled
            return;

        final AgeableMob child = event.getChild();
        if (child == null)
        {
            ItemStack eggStack = DragonEggItem.getStack(getType());
            net.minecraft.world.entity.item.ItemEntity eggItem = new net.minecraft.world.entity.item.ItemEntity(world, getX(), getY(), getZ(), eggStack);
            eggItem.setDeltaMovement(0, getBbHeight() / 3, 0);
            world.addFreshEntity(eggItem);
        }
        else
        {
            child.setBaby(true);
            child.moveTo(getX(), getY(), getZ(), 0, 0);
            world.addFreshEntityWithPassengers(child);
        }

        breedCount++;
        ((AbstractDragonEntity) mate).breedCount++;

        ServerPlayer serverPlayer = getLoveCause();

        if (serverPlayer == null && mate.getLoveCause() != null)
            serverPlayer = mate.getLoveCause();

        if (serverPlayer != null)
        {
            serverPlayer.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverPlayer, this, mate, child);
        }

        setAge(6000);
        mate.setAge(6000);
        resetLove();
        mate.resetLove();
        world.broadcastEntityEvent(this, (byte) 18);
        if (world.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))
            world.addFreshEntity(new ExperienceOrb(world, getX(), getY(), getZ(), random.nextInt(7) + 1));
    }

    @Override
    protected void addPassenger(Entity passenger)
    {
        super.addPassenger(passenger);
        if (getControllingPassenger() == passenger && isOwnedBy((LivingEntity) passenger))
        {
            stopInPlace();
            setSit(false);
            clearHome();
        }
    }

    /**
     * Get the player potentially controlling this dragon
     * {@code null} if its not a player or no controller at all.
     */
    @Nullable
    public Player getControllingPlayer()
    {
        Entity passenger = getControllingPassenger();
        if (passenger instanceof Player) return (Player) passenger;
        return null;
    }

    @Override
    public void stopInPlace() {
        jumping = false;
        navigation.stop();
        setTarget(null);
        setZza(0);
        setYya(0);
    }

    public boolean isIdling()
    {
        return getNavigation().isDone() && getTarget() == null && !hasControllingPassenger() && !isInWaterOrBubble() && !isFlying();
    }

    /**
     * A universal getter for the position of the mouth on the dragon.
     * This is prone to be inaccurate, but can serve good enough for most things
     * If a more accurate position is needed, best to override and adjust accordingly.
     *
     * @return An approximate position of the mouth of the dragon
     */
    public Vec3 getApproximateMouthPos()
    {
        Vec3 position = getEyePosition(1).subtract(0, 0.75d, 0);
        double dist = (getBbWidth() / 2) + 0.75d;
        return position.add(calculateViewVector(getXRot(), getYHeadRot()).scale(dist));
    }

    @Override
    public ItemStack getPickedResult(HitResult target)
    {
        return new ItemStack(SpawnEggItem.byId(getType()));
    }

    public List<LivingEntity> getEntitiesNearby(double radius, Predicate<LivingEntity> filter)
    {
        return level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(radius), filter.and(e -> e != this));
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean isAlliedTo(Entity entity)
    {
        if (entity == this) return true;
        if (entity instanceof LivingEntity && isOwnedBy(((LivingEntity) entity))) return true;
        if (entity instanceof TamableAnimal && getOwner() != null && getOwner().equals(((TamableAnimal) entity).getOwner()))
            return true;
        return entity.isAlliedTo(getTeam());
    }

    @Override
    public void playSound(SoundEvent soundIn, float volume, float pitch)
    {
        playSound(soundIn, volume, pitch, false);
    }

    public void playSound(SoundEvent sound, float volume, float pitch, boolean local)
    {
        if (isSilent()) return;

        volume *= getSoundVolume();
        pitch *= getVoicePitch();

        if (local) level().playLocalSound(this, sound, getSoundSource(), volume, pitch);
        else level().playSound(null, getX(), getY(), getZ(), sound, getSoundSource(), volume, pitch);
    }

    @Override
    public void playAmbientSound()
    {
        if (!isSleeping()) super.playAmbientSound();
    }

    public void flapWings()
    {
        playSound(WRSounds.WING_FLAP.value(), 3, 1, true);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source)
    {
        if (isRiding() && source == damageSources().inWall()) return true;
        if (isImmuneToArrows() && source == damageSources().cactus()) return true;
        return super.isInvulnerableTo(source);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data)
    {
        if (hasGender()) setGender(random.nextBoolean());
        if (hasVariants()) setVariant(determineVariant());

        applyAttributes();
        setHealth(getMaxHealth());

        return super.finalizeSpawn(world, difficulty, reason, data);
    }

    /**
     * This method is called after the entity is read, and after the entity initally spawns.
     * It is intended to modify the base attributes based on the entity after it has been fully constructed (and guaranteed to spawn)
     */
    public void applyAttributes()
    {
    }

    public int determineVariant()
    {
        return 0;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return super.canBeCollidedWith() && !isRiding();
    }

    //every single time I have to deal with someone's else mod I have to copypaste something from one of mines, bruh
    //original was replaced because it was broken - Nord
    @Override
    public boolean isControlledByLocalInstance() {
        if (hasControllingPassenger()
                && (getControllingPassenger() instanceof Player player && player.isLocalPlayer() || !level().isClientSide())) return true;
        return super.isControlledByLocalInstance();
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger()
    {
        return getFirstPassenger() instanceof LivingEntity mob ? mob : null;
    }

    @Override
    protected boolean canRide(Entity entityIn)
    {
        return false;
    }

    @Override
    public boolean onClimbable()
    {
        return false;
    }

    /**
     * Recieve the keybind message from the current controlling passenger.
     *
     * @param key     shut up
     * @param mods    the modifiers that is pressed when this key was pressed (e.g. shift was held, ctrl etc {@link org.lwjgl.glfw.GLFW})
     * @param pressed true if pressed, false if released. pretty straight forward idk why ur fucking asking.
     */
    public void recievePassengerKeybind(int key, int mods, boolean pressed)
    {
    }

    public boolean defendsHome()
    {
        return false;
    }

    /**
     * Sort of misleading name. if this is true, then {@link Mob#serverAiStep()} is not ticked:
     * which tl;dr does not update any AI including Goal Selectors, Pathfinding, Moving, etc.
     * Do not perform any AI actions while: Not Sleeping; not being controlled, etc.
     */
    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || isSleeping() || isRiding();
    }

    public boolean canFly()
    {
        return !isBaby() && !isUnderWater() && !isRiding();
    }

    /**
     * Get the motion this entity performs when jumping
     */
    @Override
    protected float getJumpPower()
    {
        if (canFly()) return (getBbHeight() * getBlockJumpFactor()) * 0.6f;
        else return super.getJumpPower();
    }

    public boolean liftOff()
    {
        if (!canFly()) return false;
        if (!onGround()) return true; // We can't lift off the ground in the air...

        int heightDiff = level().getHeight(Heightmap.Types.MOTION_BLOCKING, (int) getX(), (int) getZ()) - (int) getY();
        if (heightDiff > 0 && heightDiff <= getFlightThreshold())
            return false; // position has too low of a ceiling, can't fly here.

        setSit(false);
        setSleeping(false);
        jumpFromGround();
        return true;
    }

    @Override // Disable fall calculations if we can fly (fall damage etc.)
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource)
    {
        if (canFly()) return false;
        return super.causeFallDamage(distance - (int) (getBbHeight() * 0.8), damageMultiplier, damageSource);
    }

    public float getFlightThreshold() {
        return getBbHeight();
    }

    public void setMountCameraAngles(boolean backView, CalculateDetachedCameraDistanceEvent event)
    {
    }

    public boolean isImmuneToArrows()
    {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public void addScreenInfo(StaffScreen screen)
    {
        screen.addAction(StaffAction.HOME);
        screen.addAction(StaffAction.SIT);

        screen.addTooltip(Component.literal(Character.toString('\u2764'))
                .withStyle(ChatFormatting.RED)
                .append(Component.literal(String.format(" %s / %s", (int) (getHealth() / 2), (int) getMaxHealth() / 2)).withStyle(ChatFormatting.WHITE))
                .getString());
        if (hasGender())
        {
            boolean isMale = isMale();
            screen.addTooltip(Component.translatable("entity.wyrmroost.dragons.gender." + (isMale? "male" : "female"))
                    .withStyle(isMale? ChatFormatting.DARK_AQUA : ChatFormatting.RED).getString());
        }
    }

    public void addContainerInfo(DragonInvContainer container)
    {
        container.makePlayerSlots(container.playerInv, 17, 136);
    }

    public void onInvContentsChanged(int slot, ItemStack stack, boolean onLoad)
    {
    }

    @Override
    public EntityDimensions getDimensions(Pose poseIn)
    {
        EntityDimensions size = getType().getDimensions().scale(getScale());
        if (isInSittingPose() || isSleeping()) size = size.scale(1, 0.5f);
        return size;
    }

    @Override
    protected float sanitizeScale(float scale) {
        return super.sanitizeScale(scale) * getAgeScale();
    }

    @Override
    public int getBaseExperienceReward()
    {
        return Math.max((int) ((getBbWidth() * getBbHeight()) * 0.25) + random.nextInt(3), super.getBaseExperienceReward());
    }


    @Override
    public boolean isFood(ItemStack stack) {
        return isFoodItem(stack);
    }

    public abstract boolean isFoodItem(ItemStack stack);
    public abstract boolean isBreedingItem(ItemStack stack);

    // ================================
    //        Entity Animation
    // ================================

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
    public void setAnimation(Animation animation)
    {
        if (animation == null)
            animation = NO_ANIMATION;
        setAnimationTick(0);
        this.animation = animation;
    }

    @Override
    public Animation[] getAnimations()
    {
        return new Animation[0];
    }

    @SuppressWarnings("unused")
    public static boolean canFlyerSpawn(EntityType<? extends AbstractDragonEntity> type, ServerLevelAccessor world, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return world.getBlockState(pos.below()).getFluidState().isEmpty();
    }

    public static AttributeSupplier.Builder createDragonAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.SCALE, 1)
                .add(Attributes.STEP_HEIGHT, 1);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        if (level().isClientSide() || invHandler == null || getOwner() != player) return null;
        return new DragonInvContainer(invHandler, inventory, i);
    }

    public abstract boolean hasGender();
    public abstract boolean mayFly();
    public abstract boolean maySleep();
    public abstract boolean hasVariants();

    boolean canRidePlayers() {
        return false;
    }

    public boolean canRestUnderWater() {
        return false;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return 0;
    }
}
