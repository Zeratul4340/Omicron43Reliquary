package net.omicron43.reliquarymod.effect;

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

}
