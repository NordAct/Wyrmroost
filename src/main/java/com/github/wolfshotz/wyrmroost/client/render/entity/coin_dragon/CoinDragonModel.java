package com.github.wolfshotz.wyrmroost.client.render.entity.coin_dragon;

import com.github.wolfshotz.wyrmroost.client.model.WREntityModel;
import com.github.wolfshotz.wyrmroost.client.model.WRModelPart;
import com.github.wolfshotz.wyrmroost.entities.dragon.CoinDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;

/**
 * WRCoinDragon - Ukan
 * Created using Tabula 8.0.0
 */
public class CoinDragonModel extends WREntityModel<CoinDragonEntity>
{
    public final WRModelPart body1;
    public final WRModelPart body2;
    public final WRModelPart armL;
    public final WRModelPart armR;
    public final WRModelPart wingL;
    public final WRModelPart wingR;
    public final WRModelPart neck1;
    public final WRModelPart coin;
    public final WRModelPart tail1;
    public final WRModelPart legL;
    public final WRModelPart legR;
    public final WRModelPart tail2;
    public final WRModelPart tail3;
    public final WRModelPart footL;
    public final WRModelPart footR;
    public final WRModelPart head;
    public final WRModelPart eyeL;
    public final WRModelPart eyeR;
    public final WRModelPart[] tails;

    public CoinDragonModel()
    {
        super(50, 15);
        this.body1 = new WRModelPart(this, 0, 0);
        this.body1.setRotationPoint(0.0F, 19.1F, 0.0F);
        this.body1.addBox(-1.0F, -1.0F, -1.5F, 2.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(body1, -0.6981317007977318F, 0.0F, 0.0F);
        this.armR = new WRModelPart(this, 11, 6);
        this.armR.mirror = true;
        this.armR.setRotationPoint(-1.0F, 0.0F, -0.8F);
        this.armR.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(armR, -0.5235987755982988F, -0.6981317007977318F, 0.5235987755982988F);
        this.eyeL = new WRModelPart(this, 31, 4);
        this.eyeL.setRotationPoint(0.7F, -0.4F, -0.7F);
        this.eyeL.addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(eyeL, 0.6829473549475088F, 0.3186971254089062F, 0.0F);
        this.legR = new WRModelPart(this, 0, 6);
        this.legR.mirror = true;
        this.legR.setRotationPoint(-1.0F, -0.5F, 2.0F);
        this.legR.addBox(-0.5F, 0.0F, -2.0F, 1.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(legR, 1.5707963267948966F, 0.0F, 0.0F);
        this.armL = new WRModelPart(this, 11, 6);
        this.armL.setRotationPoint(1.0F, 0.0F, -0.8F);
        this.armL.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(armL, -0.5235987755982988F, 0.6981317007977318F, -0.5235987755982988F);
        this.head = new WRModelPart(this, 36, 0);
        this.head.setRotationPoint(0.0F, 0.0F, -1.7F);
        this.head.addBox(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(head, 0.9773843811168246F, 0.0F, 0.0F);
        this.wingL = new WRModelPart(this, 17, 1);
        this.wingL.setRotationPoint(0.7F, -0.7F, -0.8F);
        this.wingL.addBox(0.0F, -3.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(wingL, 0.3186971254089062F, 0.0F, 0.956091342937205F);
        this.neck1 = new WRModelPart(this, 30, 0);
        this.neck1.setRotationPoint(0.0F, -0.3F, -1.3F);
        this.neck1.addBox(-0.5F, -0.5F, -2.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(neck1, -0.17453292519943295F, 0.0F, 0.0F);
        this.tail3 = new WRModelPart(this, 22, 0);
        this.tail3.setRotationPoint(0.01F, 0.01F, 2.5F);
        this.tail3.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tail3, 0.17453292519943295F, 0.0F, 0.0F);
        this.legL = new WRModelPart(this, 0, 6);
        this.legL.setRotationPoint(1.0F, -0.5F, 2.0F);
        this.legL.addBox(-0.5F, 0.0F, -2.0F, 1.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(legL, 1.5707963267948966F, 0.0F, 0.0F);
        this.footL = new WRModelPart(this, 6, 6);
        this.footL.setRotationPoint(0.0F, 2.0F, -2.0F);
        this.footL.addBox(-0.5F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(footL, 0.3490658503988659F, 0.0F, 0.0F);
        this.body2 = new WRModelPart(this, 11, 0);
        this.body2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body2.addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F, 0.1F, 0.1F, 0.0F);
        this.setRotateAngle(body2, -0.3490658503988659F, 0.0F, 0.0F);
        this.eyeR = new WRModelPart(this, 31, 4);
        this.eyeR.mirror = true;
        this.eyeR.setRotationPoint(-0.7F, -0.4F, -0.7F);
        this.eyeR.addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(eyeR, 0.6829473549475088F, -0.3186971254089062F, 0.0F);
        this.wingR = new WRModelPart(this, 17, 1);
        this.wingR.setRotationPoint(-0.7F, -0.7F, -0.8F);
        this.wingR.addBox(0.0F, -3.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(wingR, 0.3186971254089062F, 0.0F, -0.956091342937205F);
        this.coin = new WRModelPart(this, 30, 5);
        this.coin.mirror = true;
        this.coin.setRotationPoint(0.0F, 1.0F, -1.2F);
        this.coin.addBox(-2.5F, 0.0F, 0.0F, 5.0F, 0.5F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(coin, -0.8726646259971648F, 0.0F, 0.0F);
        this.footR = new WRModelPart(this, 6, 6);
        this.footR.setRotationPoint(0.0F, 2.0F, -2.0F);
        this.footR.addBox(-0.5F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(footR, 0.3490658503988659F, 0.0F, 0.0F);
        this.tail1 = new WRModelPart(this, 22, 0);
        this.tail1.setRotationPoint(0.0F, -0.1F, 2.5F);
        this.tail1.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tail1, 0.8726646259971648F, 0.0F, 0.0F);
        this.tail2 = new WRModelPart(this, 22, 0);
        this.tail2.mirror = true;
        this.tail2.setRotationPoint(0.01F, 0.01F, 2.51F);
        this.tail2.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tail2, 0.17453292519943295F, 0.0F, 0.0F);
        this.body1.addChild(this.armR);
        this.head.addChild(this.eyeL);
        this.body2.addChild(this.legR);
        this.body1.addChild(this.armL);
        this.neck1.addChild(this.head);
        this.body1.addChild(this.wingL);
        this.body1.addChild(this.neck1);
        this.tail2.addChild(this.tail3);
        this.body2.addChild(this.legL);
        this.legL.addChild(this.footL);
        this.body1.addChild(this.body2);
        this.head.addChild(this.eyeR);
        this.body1.addChild(this.wingR);
        this.body1.addChild(this.coin);
        this.legR.addChild(this.footR);
        this.body2.addChild(this.tail1);
        this.tail1.addChild(this.tail2);

        this.tails = new WRModelPart[] {tail1, tail2, tail3};
        setDefaultPose();
    }

    @Override
    public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int packedLightIn, int packedOverlayIn, int color)
    {
        body1.render(ms, buffer, packedLightIn, packedOverlayIn, color);
    }

    @Override
    public void setupAnim(CoinDragonEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        float flap = Mth.cos(ageInTicks * 3f) * 0.6f + 0.75f;
        wingL.zRot = flap;
        wingR.zRot = -flap;

        legR.xRot = legL.xRot = Mth.cos(ageInTicks * 0.15f + 1) * 0.05f + 1.75f;

        for (int i = 1; i < tails.length + 1; i++) // move the tail a bit
            tails[i - 1].xRot = Mth.cos(ageInTicks * 0.2f + 0.8f * -i) * 0.1f + 0.35f;

        coin.xRot = Mth.cos(ageInTicks * 0.15f) * 0.08f - 0.875f;
    }
}
