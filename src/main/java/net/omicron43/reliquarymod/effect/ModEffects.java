package net.omicron43.reliquarymod.effect;

import net.omicron43.reliquarymod.Omicron43Reliquary;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

public class ModEffects {
    public static final RegistryEntry<StatusEffect> DISINTEGRATION = ModEffects.registerStatusEffect("disintegration",
            new DisintegrationStatusEffect(StatusEffectCategory.HARMFUL, 14969));

    public static RegistryEntry<StatusEffect> registerStatusEffect(String id, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Omicron43Reliquary.id(id), statusEffect);
    }


    public static void registerEffects() {
        Omicron43Reliquary.LOGGER.info("Registering effects for " + Omicron43Reliquary.MOD_ID);
    }
}
