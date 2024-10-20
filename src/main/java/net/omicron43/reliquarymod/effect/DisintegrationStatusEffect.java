package net.omicron43.reliquarymod.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.omicron43.reliquarymod.server.misc.DamageTypes;

public class DisintegrationStatusEffect extends StatusEffect {
    protected DisintegrationStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof LivingEntity) {
            if (amplifier >= 5) {
                entity.damage(DamageTypes.disintegrating(entity.getWorld().getRegistryManager()), (entity.getMaxHealth() * 999999f));
            }
        }
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        int i = 50 >> amplifier;
        if (i > 0) {
            return duration % i == 0;
        }
        return true;
    }
}
