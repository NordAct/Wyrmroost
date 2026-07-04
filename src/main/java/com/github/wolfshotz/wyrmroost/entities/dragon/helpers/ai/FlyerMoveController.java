package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai;

import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlyerMoveController extends MoveControl
{
    private final AbstractDragonEntity dragon;

    public FlyerMoveController(AbstractDragonEntity mob)
    {
        super(mob);
        this.dragon = mob;
    }

    public void tick()
    {
        if (dragon.isControlledByLocalInstance())
        {
            operation = Operation.WAIT;
            return;
        }

        if (operation == Operation.MOVE_TO)
        {
            double x = wantedX - dragon.getX();
            double y = wantedY - dragon.getY();
            double z = wantedZ - dragon.getZ();
            double distSq = x * x + y * y + z * z;
            if (distSq < 2.5000003E-7)
            {
                dragon.setZza(0f);
                return;
            }
            if (y > dragon.getFlightThreshold() + 1) dragon.setFlying(true);

            float speed;

            if (dragon.isFlying())
            {
                if (!dragon.getLookControl().isLookingAtTarget())
                    dragon.getLookControl().setLookAt(wantedX, wantedY, wantedZ, dragon.getMaxHeadYRot(), 75);

                speed = (float) (dragon.getAttributeValue(Attributes.FLYING_SPEED) * this.speedModifier) / 0.225f;
                if (y != 0) dragon.setYya(y > 0? speed : -speed);
            }
            else
            {
                speed = (float) (this.speedModifier * dragon.getAttributeValue(Attributes.MOVEMENT_SPEED));
                BlockPos blockpos = mob.blockPosition();
                BlockState blockstate = mob.level().getBlockState(blockpos);
                VoxelShape voxelshape = blockstate.getCollisionShape(mob.level(), blockpos);
                if (y > (double)mob.maxUpStep() && x * x + z * z < (double)Math.max(1.0F, mob.getBbWidth()) || !voxelshape.isEmpty() && mob.getY() < voxelshape.max(Direction.Axis.Y) + (double)blockpos.getY() && !blockstate.is(BlockTags.DOORS) && !blockstate.is(BlockTags.FENCES)) {
                    mob.getJumpControl().jump();
                    operation = MoveControl.Operation.JUMPING;
                }
            }
            dragon.setYRot(rotlerp(dragon.getYRot(), (float) (Mth.atan2(z, x) * (180f / Mafs.PI)) - 90f, dragon.getYawRotationSpeed()));
            dragon.setSpeed(speed);
            operation = Operation.WAIT;
        }
        else
        {
            dragon.setSpeed(0);
            dragon.setXxa(0);
            dragon.setYya(0);
            dragon.setZza(0);
        }
    }
}
