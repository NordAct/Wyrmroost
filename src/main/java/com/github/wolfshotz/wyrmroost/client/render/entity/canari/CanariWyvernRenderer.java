package com.github.wolfshotz.wyrmroost.client.render.entity.canari;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.client.render.entity.AbstractDragonRenderer;
import com.github.wolfshotz.wyrmroost.entities.dragon.CanariWyvernEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

public class CanariWyvernRenderer extends AbstractDragonRenderer<CanariWyvernEntity, CanariWyvernModel>
{
    // Easter egg
    private static final ResourceLocation LADY = resource("lady.png");
    private static final ResourceLocation RUDY = resource("rudy.png");

    private static final ResourceLocation[] TEXTURES = new ResourceLocation[10];

    public CanariWyvernRenderer(EntityRendererProvider.Context manager)
    {
        super(manager, new CanariWyvernModel(), 0.5f);
    }

    @Nullable
    @Override
    public ResourceLocation getTextureLocation(CanariWyvernEntity canari)
    {
        if (canari.hasCustomName())
        {
            String name = canari.getCustomName().getString();
            if (name.equals("Rudy")) return RUDY;
            else if (name.equals("Lady Everlyn Winklestein") && !canari.isMale()) return LADY;
        }

        int texture = Mth.clamp((canari.isMale() ? 0 : 5) + canari.getVariant(), 0, TEXTURES.length - 1);
        if (TEXTURES[texture] == null)
            return TEXTURES[texture] = resource("body_" + canari.getVariant() + (canari.isMale()? "m" : "f") + ".png");
        return TEXTURES[texture];
    }

    private static ResourceLocation resource(String png) { return Wyrmroost.rl(BASE_PATH + "canari_wyvern/" + png); }
}
