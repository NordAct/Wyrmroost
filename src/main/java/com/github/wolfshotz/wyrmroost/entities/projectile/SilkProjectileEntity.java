package com.github.wolfshotz.wyrmroost.entities.projectile;

import com.github.wolfshotz.wyrmroost.registry.WREffects;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class SilkProjectileEntity extends DragonProjectileEntity
{
    public SilkProjectileEntity(EntityType<? extends DragonProjectileEntity> type, Level worldIn)
    {
        super(type, worldIn);
    }

    @Override
    public void hit(RayTraceResult result)
    {
        super.hit(result);
        if (world.isRemote)
        {
            Vector3d pos = result.getHitVec();
            for (int i = 0; i < 20; i++)
                level.addParticle(new BlockParticleData(ParticleTypes.BLOCK, Blocks.COBWEB.defaultBlockState()), pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
        }
        else remove();
    }

    @Override
    public void onEntityImpact(Entity entity)
    {
        if (entity.getBbWidth() < 5 && entity.getBbHeight() < 5)
        {
            LivingEntity living = (LivingEntity) entity;
            living.attackEntityFrom(getDamageSource("silk"), 3f);
            living.addPotionEffect(new EffectInstance(WREffects.SILK.get(), 1200));
            living.applyKnockback((float) getMotion().length(), entity.getPosX() - getPosX(), entity.getPosZ() - getPosZ());
        }
    }

    @Override
    protected float getMotionFactor()
    {
        return 1.1f;
    }

    @Override
    public boolean isNoGravity()
    {
        return false;
    }

    @Override
    protected EffectType getEffectType()
    {
        return EffectType.RAYTRACE;
    }
}
