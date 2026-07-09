package com.github.wolfshotz.wyrmroost.entities.projectile;

import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;

public class DragonProjectileEntity extends Projectile implements IEntityWithComplexSpawn {
    @Nullable // Potentially if the dragon is unloaded, or is not synced yet.
    public AbstractDragonEntity shooter;
    public Vec3 acceleration;
    public float growthRate = 1f;
    public int life;
    public boolean hasCollided;

    protected DragonProjectileEntity(EntityType<? extends Projectile> type, Level world) { super(type, world); }

    public DragonProjectileEntity(EntityType<? extends DragonProjectileEntity> type, AbstractDragonEntity shooter, Vec3 position, Vec3 velocity)
    {
        super(type, shooter.level());

        velocity = velocity.add(getRandom().nextGaussian() * getAccelerationOffset(), getRandom().nextGaussian() * getAccelerationOffset(), getRandom().nextGaussian() * getAccelerationOffset());
        double length = velocity.length();
        this.acceleration = new Vec3(velocity.x / length * getMotionFactor(), velocity.y / length * getMotionFactor(), velocity.z / length * getMotionFactor());

        this.shooter = shooter;
        this.life = 50;

        setDeltaMovement(getDeltaMovement().add(acceleration));
        position = position.add(getDeltaMovement());

        Vec3 motion = getDeltaMovement();
        float x = (float) (motion.x - position.x);
        float y = (float) (motion.y - position.y);
        float z = (float) (motion.z - position.z);
        float planeSqrt = Mth.sqrt(x * x + z * z);
        float yaw = (float) Mth.atan2(z, x) * 180f / Mafs.PI - 90f;
        float pitch = (float) -(Mth.atan2(y, planeSqrt) * 180f / Mafs.PI);

        moveTo(position.x, position.y, position.z, yaw, pitch);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public void tick()
    {
        if ((!level().isClientSide() && (!shooter.isAlive() || tickCount > life || tickCount > getMaxLife())) || !level().isLoaded(blockPosition()))
        {
            discard();
            return;
        }

        super.tick();
        if (growthRate != 1) refreshDimensions();

        switch (getEffectType()) {
            case RAYTRACE:
            {
                HitResult rayTrace = ProjectileUtil.getHitResultOnMoveVector(this, this::canImpactEntity);
                if (rayTrace.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, rayTrace))
                    hit(rayTrace);
                break;
            }
            case COLLIDING:
            {
                AABB box = getBoundingBox().inflate(0.05);
                for (Entity entity : level().getEntities(this, box, this::canImpactEntity))
                    onEntityImpact(entity);

                Vec3 position = position();
                Vec3 end = position.add(getDeltaMovement());
                BlockHitResult rtr = level().clip(new ClipContext(position, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                if (rtr.getType() != HitResult.Type.MISS) onBlockImpact(rtr.getBlockPos(), rtr.getDirection());
            }
            default:
                break;
        }

        Vec3 motion = getDeltaMovement();
        if (!isNoGravity()) setDeltaMovement(motion = motion.add(0, -0.05, 0));
        double x = getX() + motion.x;
        double y = getY() + motion.y;
        double z = getZ() + motion.z;

        if (isInWater())
        {
            setDeltaMovement(motion.scale(0.95f));
            for (int i = 0; i < 4; ++i)
                level().addParticle(ParticleTypes.BUBBLE, getX() * 0.25d, getY() * 0.25d, getZ() * 0.25D, motion.x, motion.y, motion.z);
        }
        setPos(x, y, z);
    }

    public boolean canImpactEntity(Entity entity) {
        if (shooter == null) return false;
        if (entity == shooter) return false;
        if (entity instanceof OwnableEntity ownable && (ownable.getOwner() == shooter || ownable.getOwner() == shooter.getOwner())) return false;
        if (entity == shooter.getOwner()) return false;
        if (entity instanceof TraceableEntity traceable && (traceable.getOwner() == shooter || traceable.getOwner() == shooter.getOwner())) return false;
        return !entity.isAlliedTo(shooter);
    }

    public void hit(HitResult result)
    {
        HitResult.Type type = result.getType();
        if (type == HitResult.Type.BLOCK)
        {
            final BlockHitResult brtr = (BlockHitResult) result;
            onBlockImpact(brtr.getBlockPos(), brtr.getDirection());
        }
        else if (type == HitResult.Type.ENTITY) onEntityImpact(((EntityHitResult) result).getEntity());
    }

    public void onEntityImpact(Entity entity) {}

    public void onBlockImpact(BlockPos pos, Direction direction) {}

    @Override
    public void setDeltaMovement(Vec3 motionIn)
    {
        super.setDeltaMovement(motionIn);
        ProjectileUtil.rotateTowardsMovement(this, 1);
    }

    @Override
    public EntityDimensions getDimensions(Pose poseIn)
    {
        if (growthRate == 1) return getType().getDimensions();
        float size = Math.min(getBbWidth() * growthRate, 2.25f);
        return EntityDimensions.scalable(size, size);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance)
    {
        double d0 = getBoundingBox().getSize() * 4;
        if (Double.isNaN(d0)) d0 = 4;
        d0 *= 64;
        return distance < d0 * d0;
    }

    public DamageSource getDamageSource(ResourceKey<DamageType> damageType) {
        return damageSources().source(damageType, this, shooter);
    }

    protected EffectType getEffectType()
    {
        return EffectType.NONE;
    }

    protected float getMotionFactor()
    {
        return 0.95f;
    }

    protected double getAccelerationOffset()
    {
        return 0.1;
    }

    protected int getMaxLife()
    {
        return 150;
    }

    @Override
    public boolean isNoGravity()
    {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        return false;
    }

    @Override
    public float getPickRadius()
    {
        return getBbWidth();
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buf)
    {
        buf.writeInt(shooter.getId());
        buf.writeFloat(growthRate);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buf)
    {
        this.shooter = (AbstractDragonEntity) level().getEntity(buf.readInt());
        this.growthRate = buf.readFloat();
    }

    protected enum EffectType
    {
        NONE,
        RAYTRACE,
        COLLIDING
    }
}
