package com.github.wolfshotz.wyrmroost.entities.projectile.breath;

import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.entities.projectile.DragonProjectileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

public class BreathWeaponEntity extends DragonProjectileEntity
{
    public BreathWeaponEntity(EntityType<? extends DragonProjectileEntity> type, Level world) {
        super(type, world);
        growthRate = 1.025f;
    }

    public BreathWeaponEntity(EntityType<? extends DragonProjectileEntity> type, AbstractDragonEntity shooter) {
        super(type, shooter, shooter.getApproximateMouthPos(), Vec3.directionFromRotation(shooter.getXRot(), shooter.getYHeadRot()));
    }

    @Override
    public void onBlockImpact(BlockPos pos, Direction direction)
    {
//        BlockState state = world.getBlockState(pos);
//        state.onProjectileCollision(world, state, result, this); todo.. somehow

        if (!level().isClientSide() && !noPhysics && !level().getBlockState(pos).getCollisionShape(level(), pos).equals(Shapes.empty()))
        {
            setDeltaMovement(acceleration.multiply(-Math.abs(direction.getStepX()) + 1, -Math.abs(direction.getStepY()) + 1, -Math.abs(direction.getStepZ()) + 1));

            if (!hasCollided)
            {
                life = tickCount + 20;
                this.hasCollided = true;
            }
        }
    }

    @Override
    protected float getMotionFactor() { return 0.7f; }

    @Override
    protected EffectType getEffectType() { return EffectType.COLLIDING; }
}
