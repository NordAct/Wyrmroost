package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WRSounds
{
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, Wyrmroost.MOD_ID);

    public static final Holder<SoundEvent> WING_FLAP = register("wing.flap", 48);
    public static final Holder<SoundEvent> FIRE_BREATH = register("breathweapon.fire", 16);

    public static final Holder<SoundEvent> ENTITY_LDWYRM_IDLE = entity("ldwyrm.idle");

    public static final Holder<SoundEvent> ENTITY_SILVERGLIDER_IDLE = entity("silverglider.idle");
    public static final Holder<SoundEvent> ENTITY_SILVERGLIDER_HURT = entity("silverglider.hurt");
    public static final Holder<SoundEvent> ENTITY_SILVERGLIDER_DEATH = entity("silverglider.death");

    public static final Holder<SoundEvent> ENTITY_OWDRAKE_IDLE = entity("owdrake.idle");
    public static final Holder<SoundEvent> ENTITY_OWDRAKE_ROAR = entity("owdrake.roar", 32);
    public static final Holder<SoundEvent> ENTITY_OWDRAKE_HURT = entity("owdrake.hurt");
    public static final Holder<SoundEvent> ENTITY_OWDRAKE_DEATH = entity("owdrake.death");

    public static final Holder<SoundEvent> ENTITY_STALKER_IDLE = entity("stalker.idle");
    public static final Holder<SoundEvent> ENTITY_STALKER_HURT = entity("stalker.hurt");
    public static final Holder<SoundEvent> ENTITY_STALKER_DEATH = entity("stalker.death");

    public static final Holder<SoundEvent> ENTITY_BFLY_IDLE = entity("bfly.idle");
    public static final Holder<SoundEvent> ENTITY_BFLY_ROAR = entity("bfly.roar", 32);
    public static final Holder<SoundEvent> ENTITY_BFLY_HURT = entity("bfly.hurt");
    public static final Holder<SoundEvent> ENTITY_BFLY_DEATH = entity("bfly.death");

    public static final Holder<SoundEvent> ENTITY_CANARI_IDLE = entity("canari.idle");
    public static final Holder<SoundEvent> ENTITY_CANARI_HURT = entity("canari.hurt");
    public static final Holder<SoundEvent> ENTITY_CANARI_DEATH = entity("canari.death");

    public static final Holder<SoundEvent> ENTITY_DFD_IDLE = entity("dfd.idle");
    public static final Holder<SoundEvent> ENTITY_DFD_HURT = entity("dfd.hurt");
    public static final Holder<SoundEvent> ENTITY_DFD_DEATH = entity("dfd.death");

    public static final Holder<SoundEvent> ENTITY_ROYALRED_IDLE = entity("royalred.idle");
    public static final Holder<SoundEvent> ENTITY_ROYALRED_HURT = entity("royalred.hurt");
    public static final Holder<SoundEvent> ENTITY_ROYALRED_ROAR = entity("royalred.roar", 64);
    public static final Holder<SoundEvent> ENTITY_ROYALRED_DEATH = entity("royalred.death");

    public static final Holder<SoundEvent> ENTITY_ALPINE_IDLE = entity("alpine.idle");
    public static final Holder<SoundEvent> ENTITY_ALPINE_HURT = entity("alpine.hurt");
    public static final Holder<SoundEvent> ENTITY_ALPINE_ROAR = entity("alpine.roar", 64);
    public static final Holder<SoundEvent> ENTITY_ALPINE_DEATH = entity("alpine.death");

    public static final Holder<SoundEvent> ENTITY_COINDRAGON_IDLE = entity("coindragon.idle");

//    public static final RegistryObject<SoundEvent> ENTITY_ORBWYRM_IDLE = entity("orbwyrm.idle");
//    public static final RegistryObject<SoundEvent> ENTITY_ORBWYRM_HURT = entity("orbwyrm.hurt");
//    public static final RegistryObject<SoundEvent> ENTITY_ORBWYRM_HISS = entity("orbwyrm.hiss");
//    public static final RegistryObject<SoundEvent> ENTITY_ORBWYRM_DEATH = entity("orbwyrm.death");

    public static Holder<SoundEvent> register(String name, float range)
    {
        return REGISTRY.register(name, () -> SoundEvent.createVariableRangeEvent(Wyrmroost.rl(name)));
    }

    public static Holder<SoundEvent> entity(String name, float range)
    {
        return register("entity." + name, range);
    }

    public static Holder<SoundEvent> entity(String name)
    {
        return entity(name, 16);
    }
}
