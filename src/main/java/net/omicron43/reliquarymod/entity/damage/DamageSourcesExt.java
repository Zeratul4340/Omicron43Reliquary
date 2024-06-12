package net.omicron43.reliquarymod.entity.damage;

public interface DamageSourcesExt {
    default ModDamageSources pypSources() {
        throw new IllegalStateException("Not transformed");
    }
}
