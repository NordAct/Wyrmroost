package com.github.wolfshotz.wyrmroost.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

public class WRModelPart extends ModelPart {
    public float defaultRotationX;
    public float defaultRotationY;
    public float defaultRotationZ;
    public float defaultPositionX;
    public float defaultPositionY;
    public float defaultPositionZ;
    public float defaultScaleX;
    public float defaultScaleY;
    public float defaultScaleZ;
    public final int textureOffsetX;
    public final int textureOffsetY;
    public final WREntityModel<?> model;
    public boolean mirror;

    public WRModelPart(WREntityModel<?> model, int textureOffsetX, int textureOffsetY)
    {
        super(new ArrayList<>(), new HashMap<>());
        this.textureOffsetX = textureOffsetX;
        this.textureOffsetY = textureOffsetY;
        model.boxList.add(this);
        this.model = model;
    }

    public void addBox(float offX, float offY, float offZ, float width, float height, float depth, float scaleFactor) {
        addBox(offX, offY, offZ, width, height, depth, scaleFactor, scaleFactor, scaleFactor);
    }

    public void addBox(float x, float y, float z, float sizeX, float sizeY, float sizeZ, float scaleX, float scaleY, float scaleZ) {
        Cube cube = new ModelPart.Cube(textureOffsetX, textureOffsetY, x, y, z, sizeX, sizeY, sizeZ, scaleX, scaleY, scaleZ, mirror, model.textureWidth, model.textureHeight, EnumSet.allOf(Direction.class));
        cubes.add(cube);
    }

    public void setDefaultPose()
    {
        defaultRotationX = xRot;
        defaultRotationY = yRot;
        defaultRotationZ = zRot;
        defaultPositionX = x;
        defaultPositionY = y;
        defaultPositionZ = z;
        defaultScaleX = xScale;
        defaultScaleY = yScale;
        defaultScaleZ = zScale;
    }
    
    public void resetToDefaultPose()
    {
        xRot = defaultRotationX;
        yRot = defaultRotationY;
        zRot = defaultRotationZ;
        x = defaultPositionX;
        y = defaultPositionY;
        z = defaultPositionZ;
        xScale = defaultScaleX;
        yScale = defaultScaleY;
        zScale = defaultScaleZ;
    }

    public void walk(float speed, float degree, boolean invert, float offset, float weight, float limbSwing, float limbSwingAmount)
    {
        float rotation = Mth.cos(limbSwing * speed + offset) * degree * limbSwingAmount + weight * limbSwingAmount;
        xRot += invert? -rotation : rotation;
    }

    public void swing(float speed, float degree, boolean invert, float offset, float weight, float limbSwing, float limbSwingAmount)
    {
        float rotation = Mth.cos(limbSwing * speed + offset) * degree * limbSwingAmount + weight * limbSwingAmount;
        yRot += invert? -rotation : rotation;
    }

    public void flap(float speed, float degree, boolean invert, float offset, float weight, float limbSwing, float limbSwingAmount)
    {
        float rotation = Mth.cos(limbSwing * speed + offset) * degree * limbSwingAmount + weight * limbSwingAmount;
        xRot += invert? -rotation : rotation;
    }

    public void bob(float speed, float degree, boolean bounce, float limbSwing, float limbSwingAmount)
    {
        y += bounce?
                -Math.abs(Mth.sin(limbSwing * speed) * limbSwingAmount * degree) :
                Mth.sin(limbSwing * speed) * limbSwingAmount * degree - limbSwingAmount * degree;
    }

    public void addChild(WRModelPart part) {
        children.put("child" + System.nanoTime(), part);
    }

    public void setRotationPoint(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void scale(float x, float y, float z) {
        xScale = x;
        yScale = y;
        zScale = z;
    }
}
