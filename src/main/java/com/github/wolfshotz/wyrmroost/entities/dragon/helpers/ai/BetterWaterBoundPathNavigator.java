package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BetterWaterBoundPathNavigator extends WaterBoundPathNavigation {
    protected int timeoutSpeedUp = 1;
    protected boolean nodeChecked;
    protected boolean isSurroundingEmpty;
    public BetterWaterBoundPathNavigator(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int range) {
        return new PathFinder(nodeEvaluator = new AmphibiousNodeEvaluator(false), range);
    }

    @Override
    public void tick() {
        tick += timeoutSpeedUp;
        super.tick();
    }

    @Override
    public boolean isStableDestination(BlockPos pos)
    {
        return !level.getBlockState(pos.below()).isAir();
    }

    @Override
    protected boolean canUpdatePath()
    {
        return true;
    }

    @Override
    protected void followThePath() {
        Vec3 pos = getTempMobPos();
        Vec3 pathPos = path.getNextNodePos().getBottomCenter();

        double xDiff = Math.abs(pathPos.x - mob.getX());
        double yDiff = Math.abs(pathPos.y - mob.getY());
        double zDiff = Math.abs(pathPos.z - mob.getZ());

        maxDistanceToWaypoint = ((int) (mob.getBbWidth() * 0.5f + 1f));
        boolean isWithinPathPoint = xDiff < maxDistanceToWaypoint && zDiff < maxDistanceToWaypoint && yDiff < 1;

        if (isWithinPathPoint || (canCutCorner(path.getNextNode().type) && shouldTargetNextNodeInDirection(pos))) {
            path.advance();
            nodeChecked = false;
        }

        doStuckDetection(pos);
        if (pathPos.distanceTo(getTargetPos().getCenter()) > mob.position().distanceTo(getTargetPos().getCenter())) recomputePath();
    }

    protected boolean shouldTargetNextNodeInDirection(Vec3 currentPos) {
        if (path.getNextNodeIndex() + 1 >= path.getNodeCount()) return false;
        if (!mob.horizontalCollision && canMoveDirectly(currentPos, path.getNextEntityPos(mob))) return true;
        BlockPos currentNode = path.getNextNodePos();

        if (!nodeChecked) {
            isSurroundingEmpty = true;

            for (BlockPos pos : betweenClosed(mob.getBoundingBox().move(currentNode.getCenter().subtract(currentPos)))) {
                if (mob.getPathfindingMalus(nodeEvaluator.getTarget(pos.getX(), pos.getY(), pos.getZ()).type) == 0) continue;
                isSurroundingEmpty = false;
                break;
            }
            nodeChecked = true;
        }


        return currentPos.closerThan(new Vec3(currentNode.getX() + 0.5, isSurroundingEmpty ? mob.getY() + 0.5 : currentNode.getY(), currentNode.getZ() + 0.5), maxDistanceToWaypoint);
    }

    public static Iterable<BlockPos> betweenClosed(final AABB box) {
        BlockPos startPos = BlockPos.containing(box.minX, box.minY, box.minZ);
        BlockPos endPos = BlockPos.containing(box.maxX, box.maxY, box.maxZ);
        return BlockPos.betweenClosed(startPos, endPos);
    }
}
