package com.omicron43.omicrons_reliquary.init;

import com.omicron43.omicrons_reliquary.OmicronsReliquary;
import com.omicron43.omicrons_reliquary.effect.DisintegrationStatusEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, OmicronsReliquary.MOD_ID);

    public static final RegistryObject<MobEffect> DISINTEGRATION = REGISTRY.register("disintegration", DisintegrationStatusEffect::new);
}
