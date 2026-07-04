package com.github.wolfshotz.wyrmroost.client.render.entity.dragon_egg;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.client.model.WREntityModel;
import com.github.wolfshotz.wyrmroost.client.model.WRModelPart;
import com.github.wolfshotz.wyrmroost.entities.dragonegg.DragonEggEntity;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import java.util.HashMap;
import java.util.Map;

public class DragonEggRenderer extends EntityRenderer<DragonEggEntity>
{
    public static final ResourceLocation DEFAULT_TEXTURE = Wyrmroost.rl("textures/entity/dragon/dragon_egg.png");
    public static final Model MODEL = new Model();

    private static final Map<EntityType<?>, ResourceLocation> TEXTURE_MAP = new HashMap<>();

    public DragonEggRenderer(EntityRendererProvider.Context manager) { super(manager); }

    @Override
    public void render(DragonEggEntity entity, float entityYaw, float partialTicks, PoseStack ms, MultiBufferSource buffer, int packedLightIn) {
        ms.pushPose();
        scale(entity, ms);
        ms.translate(0, -1.5, 0);
        MODEL.animate(entity, partialTicks);
        VertexConsumer builder = buffer.getBuffer(MODEL.renderType(getTextureLocation(entity)));
        MODEL.renderToBuffer(ms, builder, packedLightIn, OverlayTexture.NO_OVERLAY);
        ms.popPose();

        super.render(entity, entityYaw, partialTicks, ms, buffer, packedLightIn);
    }

    @Override
    protected boolean shouldShowName(DragonEggEntity entity) { return false; }

    @Override
    public ResourceLocation getTextureLocation(DragonEggEntity entity) { return getDragonEggTexture(entity.getContainedDragon().orElse(WREntities.ALPINE.value())); }

    public static ResourceLocation getDragonEggTexture(EntityType<?> type)
    {
        return TEXTURE_MAP.computeIfAbsent(type, t ->
        {
            ResourceLocation textureLoc = Wyrmroost.rl(String.format("textures/entity/dragon/%s/egg.png", EntityType.getKey(type).getPath()));
            if (Minecraft.getInstance().getResourceManager().getResource(textureLoc).isPresent()) return textureLoc;
            return DEFAULT_TEXTURE;
        });
    }

    /**
     * Render Custom egg sizes / shapes. <P>
     * If none is defined, then calculate the model size according to egg size
     */
    private void scale(DragonEggEntity entity, PoseStack ms)
    {
        EntityDimensions size = entity.getDimensions(entity.getPose());
        if (size != null) ms.scale(size.width() * 2.95f, -(size.height() * 2), -(size.width() * 2.95f));
    }

    /**
     * WREggTemplate - Ukan
     * Created using Tabula 7.0.1
     */
    public static class Model extends WREntityModel<DragonEggEntity>
    {
        public WRModelPart base;
        public WRModelPart two;
        public WRModelPart three;
        public WRModelPart four;

        public Model()
        {
            super(64, 32);
            four = new WRModelPart(this, 0, 19);
            four.setRotationPoint(0.0F, -1.3F, 0.0F);
            four.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, 0.0F);
            two = new WRModelPart(this, 17, 0);
            two.setRotationPoint(0.0F, -1.5F, 0.0F);
            two.addBox(-2.5F, -3.0F, -2.5F, 5, 6, 5, 0.0F);
            three = new WRModelPart(this, 0, 9);
            three.setRotationPoint(0.0F, -2.0F, 0.0F);
            three.addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F);
            base = new WRModelPart(this, 0, 0);
            base.setRotationPoint(0.0F, 22.0F, 0.0F);
            base.addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F);
            three.addChild(four);
            base.addChild(two);
            two.addChild(three);

            setDefaultPose();
        }

        @Override
        public void setupAnim(DragonEggEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

        public void animate(DragonEggEntity entity, float partialTicks)
        {
            float time = entity.wiggleTime.get(partialTicks);
            base.xRot = time * entity.wiggleDirection.getStepX();
            base.zRot = time * entity.wiggleDirection.getStepZ();
        }

        @Override
        public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int packedLightIn, int packedOverlayIn, int color)
        {
            base.render(ms, buffer, packedLightIn, packedOverlayIn, color);
            base.resetToDefaultPose();
        }
    }
}
