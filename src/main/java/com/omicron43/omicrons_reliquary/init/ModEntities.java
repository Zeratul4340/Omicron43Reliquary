package com.omicron43.omicrons_reliquary.init;

import com.omicron43.omicrons_reliquary.OmicronsReliquary;
import com.omicron43.omicrons_reliquary.entity.projectile.LaserEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/*this is where all the projectiles and living things go*/
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, OmicronsReliquary.MOD_ID);

    public static final RegistryObject<EntityType<LaserEntity>> LASER = register("laser",
            EntityType.Builder.<LaserEntity>of(LaserEntity::new, MobCategory.MISC).sized(0.1f, 0.1f).fireImmune().setUpdateInterval(1));

    /*super cool way to register entities by just... doing the supplier thingy here*/
    public static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder){
        return REGISTRY.register(name, () -> entityTypeBuilder.build(OmicronsReliquary.id(name).toString()));
    }

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
