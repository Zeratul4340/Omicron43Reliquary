package net.omicron43.reliquarymod.entity.damage;

/*
* so basically i yoinked this from doctor4t's code
*
* i have like 40% of a clue what this does but for now I will kit bash the flip out of code I find to get this to work
* (pretty sure this is a dummy interface needed for mixins to work at all.)
*
* Knowledge through disintegration
 */

public interface DamageSourcesExt {
    default ModDamageSources relquarySources() {
        throw new IllegalStateException("Not transformed");
    }
}
