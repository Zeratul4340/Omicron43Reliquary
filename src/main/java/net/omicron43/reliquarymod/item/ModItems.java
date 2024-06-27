package net.omicron43.reliquarymod.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.omicron43.reliquarymod.Omicron43Reliquary;
import net.omicron43.reliquarymod.item.custom.DeleterCubeItem;

public class ModItems {
    public static final Item DELETER_CUBE = registerItem("deleter_cube",
            new DeleterCubeItem(new Item.Settings().maxDamage(4096)));

    private static void addItemsToCombatTabItemGroup(FabricItemGroupEntries entries) {
        entries.add(DELETER_CUBE);
    }

    private static void addItemsToIngredientTabItemGroup(FabricItemGroupEntries entries) {

    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Omicron43Reliquary.id(name), item);
    }

    public static void registerModItems() {
        Omicron43Reliquary.LOGGER.info("Registering items for" + Omicron43Reliquary.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientTabItemGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::addItemsToCombatTabItemGroup);
    }
}
