package com.github.wolfshotz.wyrmroost.client.render.entity.butterfly;

import com.github.wolfshotz.wyrmroost.WRConfig;
import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.client.render.entity.AbstractDragonRenderer;
import com.github.wolfshotz.wyrmroost.entities.dragon.ButterflyLeviathanEntity;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public class ButterflyLeviathanRenderer extends AbstractDragonRenderer<ButterflyLeviathanEntity, ButterflyLeviathanModel>
{
    public static final ResourceLocation BLUE = resource("body_blue.png");
    public static final ResourceLocation PURPLE = resource("body_purple.png");
    public static final ResourceLocation CHRISTMAS = resource("christmas.png");
    // Special
    public static final ResourceLocation ALBINO = resource("body_albino.png");
    public static final ResourceLocation CHRISTMAS_SPECIAL = resource("christmas_special.png");
    // Glow
    public static final ResourceLocation GLOW = resource("activated.png");
    public static final ResourceLocation CHRISTMAS_GLOW = resource("christmas_activated.png");

    private static final Material CONDUIT_CAGE_TEXTURE = new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.withDefaultNamespace("entity/conduit/cage"));
    private static final Material CONDUIT_WIND_TEXTURE = new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.withDefaultNamespace("entity/conduit/wind"));
    private static final Material CONDUIT_VERTICAL_WIND_TEXTURE = new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.withDefaultNamespace("entity/conduit/wind_vertical"));
    private static final Material CONDUIT_OPEN_EYE_TEXTURE = new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.withDefaultNamespace("entity/conduit/open_eye"));

    public ButterflyLeviathanRenderer(EntityRendererProvider.Context manager)
    {
        super(manager, new ButterflyLeviathanModel(), 2f);
        addLayer(new LightningLayer());
        addLayer(new ConduitLayer(manager));
    }

    @Override
    protected void scale(ButterflyLeviathanEntity p_115314_, PoseStack ms, float p_115316_) {
        float scale = 3;
        ms.scale(scale, scale, scale);
        super.scale(p_115314_, ms, p_115316_);
    }

    @Nullable
    @Override
    public ResourceLocation getTextureLocation(ButterflyLeviathanEntity entity)
    {
        int variant = entity.getVariant();

        if (WRConfig.deckTheHalls)
        {
            return variant == -1? CHRISTMAS_SPECIAL : CHRISTMAS;
        }

        switch (variant)
        {
            default:
            case 0:
                return BLUE;
            case 1:
                return PURPLE;
            case -1:
                return ALBINO;
        }
    }

    public static ResourceLocation resource(String png)
    {
        return Wyrmroost.rl(BASE_PATH + "butterfly_leviathan/" + png);
    }

    public class LightningLayer extends RenderLayer<ButterflyLeviathanEntity, ButterflyLeviathanModel>
    {
        public LightningLayer()
        {
            super(ButterflyLeviathanRenderer.this);
        }

        @Override
        public void render(PoseStack ms, MultiBufferSource buffer, int packedLight, ButterflyLeviathanEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
        {
            int alpha = Mth.clamp(entity.getLightningCooldown(), 1, 255);
            VertexConsumer builder = buffer.getBuffer(RenderType.EYES.apply(WRConfig.deckTheHalls? CHRISTMAS_GLOW : GLOW, RenderType.TRANSLUCENT_TRANSPARENCY));
            getModel().renderToBuffer(ms, builder, 15728640, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.color(alpha, 0xFFFFFF));
        }
    }

    public class ConduitLayer extends RenderLayer<ButterflyLeviathanEntity, ButterflyLeviathanModel>
    {
        public ModelPart conduitEye;
        public ModelPart conduitWind;
        public ModelPart conduitCage;

        public ConduitLayer(EntityRendererProvider.Context context) {
            super(ButterflyLeviathanRenderer.this);

            conduitEye = context.bakeLayer(ModelLayers.CONDUIT_EYE);
            conduitWind = context.bakeLayer(ModelLayers.CONDUIT_WIND);
            conduitCage = context.bakeLayer(ModelLayers.CONDUIT_CAGE);
        }

        @Override
        public void render(PoseStack ms, MultiBufferSource buffer, int light, ButterflyLeviathanEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float tick, float netHeadYaw, float headPitch)
        {
            if ((entity.getAnimation() == ButterflyLeviathanEntity.CONDUIT_ANIMATION && entity.getAnimationTick() < 15) || !entity.hasConduit())
                return;

            int overlay = getOverlayCoords(entity, getWhiteOverlayProgress(entity, partialTicks));
            float rotation = (tick * -0.0375f * 0.05f) * (180f / Mafs.PI);
            float translation = Mth.sin(tick * 0.1F) / 2.0F + 0.5F;
            translation = translation * translation + translation;
            if (!entity.isUnderWater()) headPitch /= 2;

            ms.pushPose();
            ms.scale(0.33f, 0.33f, 0.33f);
            ms.translate(getModel().head.x / 16, getModel().head.y / 16, getModel().head.z / 16);
            ms.mulPose(Axis.YP.rotationDegrees(netHeadYaw * 0.75f)); // mulPose();(); to match head rotations
            ms.mulPose(Axis.XP.rotationDegrees(headPitch));
            ms.translate(0,  0.5f - (entity.beachedTimer.get(partialTicks) * 1.1f), -3.65);

            // Cage
            ms.pushPose();
            ms.translate(0, (0.3F + translation * 0.2F), 0);
            Vector3f vector3f = new Vector3f(0.5F, 1.0F, 0.5F);
            vector3f.normalize();
            ms.mulPose(new Quaternionf().rotateAxis(rotation, vector3f));
            conduitCage.render(ms, CONDUIT_CAGE_TEXTURE.buffer(buffer, RenderType::entityCutoutNoCull), light, overlay);
            ms.popPose();

            // Wind
            int gen = entity.tickCount / 66 % 3;
            ms.pushPose();
            ms.translate(0, 0.5d, 0);
            if (gen == 1) ms.mulPose(Axis.XP.rotationDegrees(90));
            else if (gen == 2) ms.mulPose(Axis.ZP.rotationDegrees(90));
            VertexConsumer builder = (gen == 1? CONDUIT_VERTICAL_WIND_TEXTURE : CONDUIT_WIND_TEXTURE).buffer(buffer, RenderType::entityCutoutNoCull);
            conduitWind.render(ms, builder, light, overlay);
            ms.popPose();

            // Wind but its the second time
            ms.pushPose();
            ms.scale(0.875f, 0.875f, 0.875f);
            ms.mulPose(Axis.XP.rotationDegrees(180f));
            ms.mulPose(Axis.ZP.rotationDegrees(180f));
            conduitWind.render(ms, builder, light, overlay);
            ms.popPose();

            // Eye
            ms.pushPose();
            ms.translate(0, (0.3F + translation * 0.2F), 0);
            ms.mulPose(Axis.YN.rotationDegrees(entity.yHeadRot)); // negate stack rotation from entity for full rotation control
            ms.mulPose(Axis.YP.rotationDegrees(Minecraft.getInstance().getEntityRenderDispatcher().camera.getYRot()));
            ms.mulPose(Axis.XP.rotationDegrees(Minecraft.getInstance().getEntityRenderDispatcher().camera.getXRot()));
            ms.scale(0.8f, 0.8f, 0.8f);
            conduitEye.render(ms, CONDUIT_OPEN_EYE_TEXTURE.buffer(buffer, RenderType::entityCutoutNoCull), light, overlay);
            ms.popPose();

            ms.popPose();
        }
    }
}
