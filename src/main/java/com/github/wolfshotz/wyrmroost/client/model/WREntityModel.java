package com.github.wolfshotz.wyrmroost.client.model;

import com.github.wolfshotz.wyrmroost.util.Mafs;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public abstract class WREntityModel<T extends Entity> extends EntityModel<T>
{
    public T entity;
    public float globalSpeed = 0.5f;
    public final List<WRModelPart> boxList = new ArrayList<>();
    public float time;
    public final int textureWidth;
    public final int textureHeight;

    public WREntityModel(int textureWidth, int textureHeight) { super(RenderType::entityCutoutNoCull);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public void setDefaultPose()
    {
        for (WRModelPart box : boxList) box.setDefaultPose();
    }

    public void resetToDefaultPose()
    {
        globalSpeed = 0.5f;
        for (WRModelPart box : boxList)
            box.resetToDefaultPose();
    }

    public void setRotateAngle(WRModelPart model, float x, float y, float z)
    {
        model.xRot = x;
        model.yRot = y;
        model.zRot = z;
    }

    public void faceTarget(float yaw, float pitch, float rotationDivisor, WRModelPart... boxes)
    {
        rotationDivisor *= boxes.length;
        yaw = (float) Math.toRadians(yaw) / rotationDivisor;
        pitch = (float) Math.toRadians(pitch) / rotationDivisor;

        for (WRModelPart box : boxes)
        {
            box.xRot += pitch;
            box.yRot += yaw;
        }
    }

    /**
     * Rotate Angle X
     */
    public void walk(WRModelPart box, float speed, float degree, boolean invert, float offset, float weight, float walk, float walkAmount)
    {
        box.walk(speed, degree, invert, offset, weight, walk, walkAmount);
    }

    /**
     * Rotate Angle Z
     */
    public void flap(WRModelPart box, float speed, float degree, boolean invert, float offset, float weight, float flap, float flapAmount)
    {
        box.flap(speed, degree, invert, offset, weight, flap, flapAmount);
    }

    /**
     * Rotate Angle Y
     */
    public void swing(WRModelPart box, float speed, float degree, boolean invert, float offset, float weight, float swing, float swingAmount)
    {
        box.swing(speed, degree, invert, offset, weight, swing, swingAmount);
    }

    /**
     * Bob the box up and down
     *
     * @param bounce back and forth
     */
    public void bob(WRModelPart box, float speed, float degree, boolean bounce, float limbSwing, float limbSwingAmount)
    {
        box.bob(speed, degree, bounce, limbSwing, limbSwingAmount);
    }

    /**
     * Chain Wave (rotateAngleX)
     */
    public void chainWave(WRModelPart[] boxes, float speed, float degree, double rootOffset, float swing, float swingAmount)
    {
        float offset = calculateChainOffset(rootOffset, boxes);
        for (int index = 0; index < boxes.length; ++index)
            boxes[index].xRot += calculateChainRotation(speed, degree, swing, swingAmount, offset, index);
    }

    /**
     * Chain Swing (rotateAngleY)
     */
    public void chainSwing(WRModelPart[] boxes, float speed, float degree, double rootOffset, float swing, float swingAmount)
    {
        float offset = calculateChainOffset(rootOffset, boxes);
        for (int index = 0; index < boxes.length; ++index)
            boxes[index].yRot += calculateChainRotation(speed, degree, swing, swingAmount, offset, index);
    }

    /**
     * Chain Flap (rotateAngleZ)
     */
    public void chainFlap(WRModelPart[] boxes, float speed, float degree, double rootOffset, float swing, float swingAmount)
    {
        float offset = calculateChainOffset(rootOffset, boxes);
        for (int index = 0; index < boxes.length; ++index)
            boxes[index].zRot += calculateChainRotation(speed, degree, swing, swingAmount, offset, index);
    }

    private float calculateChainRotation(float speed, float degree, float swing, float swingAmount, float offset, int boxIndex)
    {
        return Mth.cos(swing * speed + offset * boxIndex) * swingAmount * degree;
    }

    private float calculateChainOffset(double rootOffset, WRModelPart... boxes)
    {
        return (float) rootOffset * Mafs.PI / (2f * boxes.length);
    }

    public void setTime(float x) { this.time = x; }

    public void toDefaultPose()
    {
        for (WRModelPart modelRenderer : boxList)
        {
            if (modelRenderer instanceof WRModelPart)
            {
                WRModelPart box = modelRenderer;
                box.x = Mafs.linTerp(box.x, box.defaultPositionX, time);
                box.y = Mafs.linTerp(box.y, box.defaultPositionY, time);
                box.z = Mafs.linTerp(box.z, box.defaultPositionZ, time);
                box.xRot = Mafs.linTerp(box.xRot, box.defaultRotationX, time);
                box.yRot = Mafs.linTerp(box.yRot, box.defaultRotationY, time);
                box.zRot = Mafs.linTerp(box.zRot, box.defaultRotationZ, time);
            }
        }
    }

    public void move(WRModelPart box, float x, float y, float z)
    {
        box.x += time * x;
        box.y += time * y;
        box.z += time * z;
    }

    public void rotate(WRModelPart box, float x, float y, float z)
    {
        box.xRot += time * x;
        box.yRot += time * y;
        box.zRot += time * z;
    }

    public void idle(float frame) {}

    public float getAnimationSwingDelta(float speed, float tick, float partialTick)
    {
        float end = Mth.clamp(-(tick / speed) + 1, 0, 1);
        float start = Mth.clamp(-((tick - 1f) / speed) + 1, 0, 1);
        return Mth.lerp(partialTick, start, end);
    }
}
