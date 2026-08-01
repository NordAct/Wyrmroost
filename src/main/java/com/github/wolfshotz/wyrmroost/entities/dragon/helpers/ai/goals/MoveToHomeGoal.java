package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals;

import com.github.wolfshotz.wyrmroost.config.WRConfig;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class MoveToHomeGoal extends Goal
{
    private int time;
    private final AbstractDragonEntity dragon;
    private final int TIME_UNTIL_TELEPORT = 600; // 30 seconds

    public MoveToHomeGoal(AbstractDragonEntity creatureIn)
    {
        this.dragon = creatureIn;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return dragon.getHomePos().isPresent() && !dragon.isWithinRestriction();
    }

    @Override
    public void start()
    {
        dragon.stopInPlace();
    }

    @Override
    public void stop()
    {
        this.time = 0;
    }

    @Override
    public void tick() {
        int sq = WRConfig.homeRadius * WRConfig.homeRadius;
        Vec3 home = dragon.getRestrictCenter().getBottomCenter();

        time++;
        if (dragon.distanceToSqr(home) > sq + 35 || time >= TIME_UNTIL_TELEPORT) {
            //attempt to fix home pos if it's somehow happens to be in air
            if (dragon.level().getBlockState(dragon.getRestrictCenter()).getCollisionShape(dragon.level(), dragon.getRestrictCenter()).isEmpty()) {
                BlockPos.MutableBlockPos mutableBlockPos =  dragon.getRestrictCenter().mutable();
                while (dragon.level().getBlockState(mutableBlockPos).getCollisionShape(dragon.level(), dragon.getRestrictCenter()).isEmpty() && mutableBlockPos.getY() > dragon.level().getMinBuildHeight()) {
                    mutableBlockPos.move(0, -1, 0);
                }
                if (dragon.level().getBlockState(mutableBlockPos).getCollisionShape(dragon.level(), dragon.getRestrictCenter()).isEmpty()) {
                    mutableBlockPos.setY(dragon.level().getMaxBuildHeight());
                    while (dragon.level().getBlockState(mutableBlockPos).getCollisionShape(dragon.level(), dragon.getRestrictCenter()).isEmpty() && mutableBlockPos.getY() > dragon.level().getMinBuildHeight()) {
                        mutableBlockPos.move(0, -1, 0);
                    }
                }
                if (!dragon.level().getBlockState(mutableBlockPos).getCollisionShape(dragon.level(), dragon.getRestrictCenter()).isEmpty()) dragon.setHomePos(mutableBlockPos.immutable());
            }
            if (!dragon.level().getBlockState(dragon.getRestrictCenter()).getCollisionShape(dragon.level(), dragon.getRestrictCenter()).isEmpty()) {
                dragon.trySafeTeleport(dragon.getRestrictCenter().above());
                return;
            } else time = 0;
        }
        BlockPos movePos;
        if (dragon.getNavigation().isDone() && (movePos = RandomPos.generateRandomPosTowardDirection(dragon, WRConfig.homeRadius, dragon.getRandom(), dragon.getRestrictCenter())) != null)
            dragon.getNavigation().moveTo(movePos.getX(), movePos.getY(), movePos.getZ(), 1.1);
    }
}
