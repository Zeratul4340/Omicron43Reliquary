package net.omicron43.reliquarymod.server.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.omicron43.reliquarymod.Omicron43Reliquary;

public class DamageTypes {

    public static final RegistryKey<DamageType> DISINTEGRATION = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Omicron43Reliquary.id("disintegration"));

    public static DamageSource disintegrating(DynamicRegistryManager dynamicRegistryManager) {
        return new DamageSourceRandomMessages(dynamicRegistryManager.getOptional(RegistryKeys.DAMAGE_TYPE).get().entryOf(DISINTEGRATION), 1);
    }

    private static class DamageSourceRandomMessages extends DamageSource {
        private int messageCount;

        public DamageSourceRandomMessages(RegistryEntry.Reference<DamageType> message, int messageCount) {
            super(message);
            this.messageCount = messageCount;
        }

        public DamageSourceRandomMessages(RegistryEntry.Reference<DamageType> message, Entity source, int messageCount) {
            super(message, source);
            this.messageCount = messageCount;
        }

        public Text getLocalizedDeathMessage(LivingEntity attacked) {
            int type = attacked.getRandom().nextInt(this.messageCount);
            String s = "death.attack." + this.getName() + "_" + type;
            Entity entity = this.getSource() == null ? this.getAttacker() : this.getSource();
            if (entity != null) {
                return Text.translatable(s + ".entity", attacked.getDisplayName(), entity.getDisplayName());
            } else {
                return Text.translatable(s, attacked.getDisplayName());
            }
        }
    }
}