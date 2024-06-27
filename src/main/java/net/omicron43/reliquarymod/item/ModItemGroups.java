package net.omicron43.reliquarymod.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.omicron43.reliquarymod.Omicron43Reliquary;

public class ModItemGroups {
    public static final ItemGroup RELIQUARY_GROUP = Registry.register(Registries.ITEM_GROUP,
            Omicron43Reliquary.id("reliquarymod"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.reliquarymod"))
                    .icon(() -> new ItemStack(ModItems.DELETER_CUBE)).entries((displayContext, entries) -> {
                        entries.add(ModItems.DELETER_CUBE);
                    }).build());

    public static void  registerItemGroups() {
        Omicron43Reliquary.LOGGER.info("Registering item groups for " + Omicron43Reliquary.MOD_ID);
    }
}
