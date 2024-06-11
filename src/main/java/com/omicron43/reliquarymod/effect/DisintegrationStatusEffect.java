package com.omicron43.reliquarymod.effect;

import com.omicron43.reliquarymod.entity.damage.ModDamageTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.ParticleEffect;

public class DisintegrationStatusEffect extends StatusEffect {
    protected DisintegrationStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    protected DisintegrationStatusEffect(StatusEffectCategory category, int color, ParticleEffect particleEffect) {
        super(category, color, particleEffect);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof LivingEntity) {
            if (amplifier >= 5){
                entity.damage(ModDamageTypes.of(entity, ModDamageTypes.DISINTEGRATION), (entity.getMaxHealth() * 9999f));
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
