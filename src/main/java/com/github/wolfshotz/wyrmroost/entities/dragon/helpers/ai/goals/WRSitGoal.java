package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals;

import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.gen.Heightmap;

import java.util.Random;

public class WRSitGoal extends SitWhenOrderedToGoal
{
    private final AbstractDragonEntity dragon;

    public WRSitGoal(AbstractDragonEntity dragon)
    {
        super(dragon);
        this.dragon = dragon;
    }

    public boolean shouldExecute()
    {
        if (!dragon.isTame()) return false;
        if (dragon.isInWaterOrBubbleColumn() && dragon.getCreatureAttribute() != CreatureAttribute.WATER) return false;
        if (!dragon.onGround() && !dragon.isFlying()) return false;
        LivingEntity owner = dragon.getOwner();
        if (owner == null) return true;
        return (dragon.getDistanceSq(owner) > 144d || owner.getRevengeTarget() == null) && super.shouldContinueExecuting();
    }

    @Override
    public void tick()
    {
        if (dragon.isFlying()) // get to ground first
        {
            if (dragon.getNavigator().noPath())
            {
                BlockPos pos = findLandingPos();
                dragon.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), 1.05);
            }
        }
        else dragon.func_233686_v_(true);
    }

    private BlockPos findLandingPos()
    {
        Random rand = dragon.getRNG();

        // get current entity position
        BlockPos.MutableBlockPos ground = dragon.level.getHeight(Heightmap.Type.WORLD_SURFACE, dragon.getPosition()).toMutable();

        // make sure the y value is suitable
        if (ground.getY() <= 0 || ground.getY() > dragon.getPosY() || !dragon.level.getBlockState(ground.below()).getMaterial().isSolid())
            ground.setY((int) dragon.getPosY() - 5);

        // add some variance
        int followRange = Mth.floor(dragon.getAttributeValue(Attributes.FOLLOW_RANGE));
        int ox = followRange - rand.nextInt(followRange) * 2;
        int oz = followRange - rand.nextInt(followRange) * 2;
        ground.setX(ox);
        ground.setZ(oz);

        return ground;
    }
}
