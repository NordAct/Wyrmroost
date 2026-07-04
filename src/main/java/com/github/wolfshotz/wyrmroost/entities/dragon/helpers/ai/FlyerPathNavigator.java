package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai;

import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.phys.Vec3;

/**
 * todo: I guess make our own node processor. Derivative of WalkAndSwim, just ditch all the water related shit.
 */
public class FlyerPathNavigator extends FlyingPathNavigation
{
    public FlyerPathNavigator(AbstractDragonEntity mob)
    {
        super(mob, mob.level());
    }

    @Override
    @SuppressWarnings("ConstantConditions") // IT CAN BE NULL DAMNIT
    public void tick()
    {
        if (!isDone() && canUpdatePath())
        {
            AbstractDragonEntity dragon = ((AbstractDragonEntity) mob);
            BlockPos target = getTargetPos();
            if (target != null)
            {
                mob.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), speedModifier);
                maxDistanceToWaypoint = mob.getBbWidth() * mob.getBbWidth() * dragon.getYawRotationSpeed() * dragon.getYawRotationSpeed();
                Vec3 mobPos = getTempMobPos();
                if (target.distToCenterSqr(mobPos.x, mobPos.y, mobPos.z) <= maxDistanceToWaypoint)
                    path = null;
            }
        }
    }

    @Override
    public boolean isStableDestination(BlockPos pos)
    {
        return true;
    }
}
