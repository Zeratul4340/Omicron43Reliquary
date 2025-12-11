package com.omicron43.omicrons_reliquary.effect;

import com.omicron43.omicrons_reliquary.init.ModDamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class DisintegrationStatusEffect extends MobEffect {
    public DisintegrationStatusEffect() {
        super(MobEffectCategory.HARMFUL, 14969);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if(pLivingEntity instanceof LivingEntity){
            if (pAmplifier >= 5) {
                pLivingEntity.hurt(ModDamageTypes.disintegrating(pLivingEntity.level().registryAccess()), (pLivingEntity.getMaxHealth() * 999999f));
            }
        }
    }
}
