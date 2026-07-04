package com.github.wolfshotz.wyrmroost.client.render.entity.projectile;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.projectile.DragonProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class BreathWeaponRenderer extends EntityRenderer<DragonProjectileEntity>
{
    public static final ResourceLocation BLUE_FIRE = Wyrmroost.rl("entity/projectiles/rr_breath/blue_fire");
    public static final Material BLUE_FIRE_MATERIAL = new Material(BLUE_FIRE.withSuffix(".png"), BreathWeaponRenderer.BLUE_FIRE);

    public BreathWeaponRenderer(EntityRendererProvider.Context renderManager) { super(renderManager); }

    @Override
    public void render(DragonProjectileEntity entity, float yaw, float partialTicks, PoseStack ms, MultiBufferSource typeBuffer, int packedLine)
    {
        if (entity.isOnFire()) {
            renderFire(ms, typeBuffer, entity);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(DragonProjectileEntity entity) { return null; }

    private void renderFire(PoseStack ms, MultiBufferSource typeBuffer, Entity entity)
    {
        ms.pushPose();
        float width = entity.getBbWidth() * 1.4F;
        ms.scale(width, width, width);
        float x = 0.5F;
        float height = entity.getBbHeight() / width;
        float y = 0.0F;
        ms.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        ms.translate(0, 0, (-0.3f + (float) ((int) height) * 0.02f));
        float z = 0;
        VertexConsumer vertex = BLUE_FIRE_MATERIAL.buffer(typeBuffer, RenderType::entityCutoutNoCull);
        PoseStack.Pose msEntry = ms.last();

        vertex(msEntry, vertex, x, -y, z, 1, 1);
        vertex(msEntry, vertex, -x, -y, z, 0, 1);
        vertex(msEntry, vertex, -x, 1.4f - y, z, 0, 0);
        vertex(msEntry, vertex, x, 1.4f - y, z, 1, 0);

        ms.popPose();
    }

    private static void vertex(PoseStack.Pose msEntry, VertexConsumer bufferIn, float x, float y, float z, float texU, float texV)
    {
        bufferIn.addVertex(msEntry.pose(), x, y, z).setColor(0xFFFFFFFF).setUv(texU, texV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(240).setNormal(msEntry, 0.0F, 1.0F, 0.0F);
    }
}
