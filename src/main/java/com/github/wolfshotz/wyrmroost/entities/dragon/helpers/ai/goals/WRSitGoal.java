package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals;

import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.level.levelgen.Heightmap;

public class WRSitGoal extends SitWhenOrderedToGoal
{
    private final AbstractDragonEntity dragon;

    public WRSitGoal(AbstractDragonEntity dragon)
    {
        super(dragon);
        this.dragon = dragon;
    }

    @Override
    public boolean canUse()
    {
        if (!dragon.isTame()) return false;
        if (dragon.isUnderWater() && !dragon.canRestUnderWater()) return false;
        if (!dragon.onGround() && dragon.mayFly()) return false;
        LivingEntity owner = dragon.getOwner();
        if (owner == null) return true;
        return (dragon.distanceToSqr(owner) > 144d || owner.getLastHurtByMob() == null) && dragon.isOrderedToSit();
    }

    @Override
    public void tick()
    {
        if (dragon.isFlying()) // get to ground first
        {
            if (dragon.getNavigation().isDone())
            {
                BlockPos pos = findLandingPos();
                dragon.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1.05);
            }
        }
        else dragon.setSit(true);
    }

    private BlockPos findLandingPos()
    {
        RandomSource rand = dragon.getRandom();

        // get current entity position
        BlockPos.MutableBlockPos ground = dragon.level().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, dragon.blockPosition()).mutable();

        // make sure the y value is suitable
        if (ground.getY() <= 0 || ground.getY() > dragon.getY() || !dragon.level().getBlockState(ground.below()).isSolid())
            ground.setY((int) dragon.getY() - 5);

        // add some variance
        int followRange = Mth.floor(dragon.getAttributeValue(Attributes.FOLLOW_RANGE));
        int ox = followRange - rand.nextInt(followRange) * 2;
        int oz = followRange - rand.nextInt(followRange) * 2;
        ground.setX(ox);
        ground.setZ(oz);

        return ground;
    }
}
