package net.omicron43.reliquarymod.entity.damage;

import net.omicron43.reliquarymod.Omicron43Reliquary;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

/*
 * Note for later: check net.minecraft.entity.damage.DamageEffects for custom sounds from taking the
 * specified damage type
 */

public class ModDamageSources {
    public static final RegistryKey<DamageType> DISINTEGRATION = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Omicron43Reliquary.id("disintegration"));

    private final DamageSource disintegration;

    public ModDamageSources(DamageSources damageSources) {
        this.disintegration = damageSources.create(DISINTEGRATION);
    }

    public DamageSource disintegration(){return disintegration;}
}
