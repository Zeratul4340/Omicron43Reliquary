package com.omicron43.omicrons_reliquary.init;

import com.omicron43.omicrons_reliquary.OmicronsReliquary;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OmicronsReliquary.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ITEMS = CREATIVE_MODE_TABS.register("items",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group.omicrons_reliquary.items"))
                    .icon(() -> new ItemStack(Items.NETHER_STAR))
                    .displayItems((param, output) -> ModItems.ITEMS.getEntries().forEach(registryObject -> {
                        output.accept(registryObject.get());

                        /*probably going to be useless coz I don't intend to add forge energy compat*/
                        /*var stack = new ItemStack(registryObject.get());
                        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
                            if (energy.getMaxEnergyStored() > 0) {
                                energy.receiveEnergy(Integer.MAX_VALUE, false);
                                output.accept(stack);
                            }
                        });*/
                    }))
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
