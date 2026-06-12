package com.github.wolfshotz.wyrmroost.entities.projectile.breath;

import com.github.wolfshotz.wyrmroost.WRConfig;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import net.minecraft.core.BlockPos;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FireBreathEntity extends BreathWeaponEntity
{
    public FireBreathEntity(EntityType<?> type, Level world)
    {
        super(type, world);
    }

    public FireBreathEntity(AbstractDragonEntity shooter)
    {
        super(WREntities.FIRE_BREATH.get(), shooter);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (isInWater())
        {
            if (rand.nextDouble() <= 0.25d) playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1, 1);
            for (int i = 0; i < 15; i++)
                level.addParticle(ParticleTypes.SMOKE, getPosX(), getPosY(), getPosZ(), Mafs.nextDouble(rand) * 0.2f, rand.nextDouble() * 0.08f, Mafs.nextDouble(rand) * 0.2f);
            remove();
            return;
        }

        Vector3d motion = getMotion();
        double x = getPosX() + motion.x + (rand.nextGaussian() * 0.2);
        double y = getPosY() + motion.y + (rand.nextGaussian() * 0.2) + 0.5d;
        double z = getPosZ() + motion.z + (rand.nextGaussian() * 0.2);
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
    }

    @Override
    public void onBlockImpact(BlockPos pos, Direction direction)
    {
        super.onBlockImpact(pos, direction);
        if (world.isRemote) return;

        BlockState state = level.getBlockState(pos);
        if (CampfireBlock.canLight(state))
        {
            level.setBlockState(pos, state.setValue(BlockStateProperties.LIT, true), 11);
            return;
        }

        double flammability = WRConfig.fireBreathFlammability;
        if (level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK) && WRConfig.canGrief(level) && flammability != 0) // respect game rules
        {
            BlockPos offset = pos.relative(direction);

            if (level.getBlockState(offset).isAir(level, offset) && (flammability == 1 || rand.nextDouble() <= flammability))
                level.setBlockState(offset, BaseFireBlock.getFireForPlacement(level, offset), 11);
        }
    }

    @Override
    public void onEntityImpact(Entity entity)
    {
        if (world.isRemote) return;

        float damage = (float) shooter.getAttributeValue(WREntities.Attributes.PROJECTILE_DAMAGE.get());
        if (level.isRainingAt(entity.getPosition())) damage *= 0.75f;

        if (entity.isImmuneToFire()) damage *= 0.25; // impact damage
        else entity.setFire(8);

        entity.attackEntityFrom(getDamageSource(rand.nextDouble() > 0.2? "fireBreath0" : "fireBreath1"), damage);
    }

    @Override
    public DamageSource getDamageSource(String name)
    {
        return super.getDamageSource(name).setFireDamage();
    }

    @Override // Because we do it better.
    public boolean canRenderOnFire()
    {
        return false;
    }

    @Override
    public boolean isBurning()
    {
        return true;
    }
}
