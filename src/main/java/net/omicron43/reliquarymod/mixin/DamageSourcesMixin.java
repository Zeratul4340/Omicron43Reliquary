package net.omicron43.reliquarymod.mixin;

import net.minecraft.entity.damage.DamageSources;
import net.minecraft.registry.DynamicRegistryManager;
import net.omicron43.reliquarymod.entity.damage.DamageSourcesExt;
import net.omicron43.reliquarymod.entity.damage.ModDamageSources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageSources.class)
public class DamageSourcesMixin implements DamageSourcesExt {
    @Unique
    private ModDamageSources reliquarymod$damageSources;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(DynamicRegistryManager registryManager, CallbackInfo ci) {
        this.reliquarymod$damageSources = new ModDamageSources((DamageSources) (Object) this);
    }
    @Override
    public ModDamageSources relquarySources() {
        return this.reliquarymod$damageSources;
    }
}
