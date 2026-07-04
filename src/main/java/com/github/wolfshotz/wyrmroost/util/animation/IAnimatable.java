package com.github.wolfshotz.wyrmroost.util.animation;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public interface IAnimatable {
    EntityCapability<IAnimatable, Void> CAPABILITY = EntityCapability.create(
            Wyrmroost.rl("animatable"),
            IAnimatable.class,
            Void.class
    );

    Animation NO_ANIMATION = new Animation(0)
    {
        @Override
        public String toString() { return "NO_ANIMATION"; }
    };

    int getAnimationTick();

    void setAnimationTick(int tick);

    Animation getAnimation();

    void setAnimation(Animation animation);

    Animation[] getAnimations();

    default boolean noActiveAnimation() { return getAnimation() == NO_ANIMATION; }

    default void updateAnimations()
    {
        Animation current = getAnimation();
        if (current != NO_ANIMATION)
        {
            int tick = getAnimationTick() + 1;
            if (tick >= current.getDuration())
            {
                setAnimation(NO_ANIMATION);
                tick = 0;
            }
            setAnimationTick(tick);
        }
    }

    @SubscribeEvent
    static void registerCapability(RegisterCapabilitiesEvent event) {
        WREntities.REGISTRY.getEntries().forEach(holder -> {
            if (IAnimatable.class.isAssignableFrom(holder.value().getBaseClass())) event.registerEntity(CAPABILITY, holder.value(), ((o, unused) -> null));
        });
    }

    class CapImpl implements IAnimatable {

        private int animationTick = 0;
        private Animation animation;

        @Override
        public int getAnimationTick()
        {
            return animationTick;
        }

        @Override
        public void setAnimationTick(int tick)
        {
            this.animationTick = tick;
        }

        @Override
        public Animation getAnimation()
        {
            return animation;
        }

        @Override
        public void setAnimation(Animation animation)
        {
            this.animation = animation;
        }

        @Override
        public Animation[] getAnimations()
        {
            return new Animation[0];
        }
    }
}
