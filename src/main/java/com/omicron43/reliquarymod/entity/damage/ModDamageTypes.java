package com.omicron43.reliquarymod.entity.damage;

import com.omicron43.reliquarymod.Omicron43Reliquary;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/*
* Note for later: check net.minecraft.entity.damage.DamageEffects for custom sounds from taking the
* specified damage type
*/

public interface ModDamageTypes extends DamageTypes {
    public static final RegistryKey<DamageType> DISINTEGRATION = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Omicron43Reliquary.MOD_ID, "disintegration"));

    public static void bootstrap(Registerable<DamageType> damageTypeRegisterable) {
        damageTypeRegisterable.register(DISINTEGRATION, new DamageType("disintegration", 0.1f));
    }

    public static DamageSource of(LivingEntity entity, RegistryKey<DamageType> key){
        return new DamageSource(entity.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }
    public static DamageSource of(World world, RegistryKey<DamageType> key){
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }
}
