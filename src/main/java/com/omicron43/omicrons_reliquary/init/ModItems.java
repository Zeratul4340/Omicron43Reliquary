package com.omicron43.omicrons_reliquary.init;

import com.omicron43.omicrons_reliquary.OmicronsReliquary;
import com.omicron43.omicrons_reliquary.item.relic.DeleterCubeItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
    DeferredRegister.create(ForgeRegistries.ITEMS, OmicronsReliquary.MOD_ID);

    public static final RegistryObject<Item> DELETER_CUBE = ITEMS.register("deleter_cube",
            () -> new DeleterCubeItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
