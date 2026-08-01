package com.github.wolfshotz.wyrmroost.client.render.entity.coin_dragon;

import com.github.wolfshotz.wyrmroost.config.WRConfig;
import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.CoinDragonEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class CoinDragonRenderer extends MobRenderer<CoinDragonEntity, CoinDragonModel>
{
    private static final ResourceLocation CHRISTMAS = Wyrmroost.rl("textures/entity/dragon/coin_dragon/christmas.png");
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[5];

    static
    {
        for (int i = 0; i < TEXTURES.length; i++)
            TEXTURES[i] = Wyrmroost.rl("textures/entity/dragon/coin_dragon/body_" + i + ".png");
    }

    public CoinDragonRenderer(EntityRendererProvider.Context renderManagerIn)
    {
        super(renderManagerIn, new CoinDragonModel(), 0.25f);
    }

    @Override
    public ResourceLocation getTextureLocation(CoinDragonEntity entity) {
        return WRConfig.deckTheHalls? CHRISTMAS : TEXTURES[Mth.clamp(entity.getVariant(), 0, TEXTURES.length - 1)];
    }
}
