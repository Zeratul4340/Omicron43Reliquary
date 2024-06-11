package com.omicron43.reliquarymod.entity.damage;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.registry.DynamicRegistryManager;

public class ModDamageSources extends DamageSources {
    private final DamageSource disintegration;

    public ModDamageSources(DynamicRegistryManager registryManager) {
        super(registryManager);
        this.disintegration = this.create(ModDamageTypes.DISINTEGRATION);
    }

    public DamageSource disintegration(){return this.disintegration;}
}
