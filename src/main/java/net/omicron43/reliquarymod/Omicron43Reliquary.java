package net.omicron43.reliquarymod;

import net.omicron43.reliquarymod.client.component.ModDataComponentTypes;
import net.omicron43.reliquarymod.effect.ModEffects;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import net.omicron43.reliquarymod.item.ModItemGroups;
import net.omicron43.reliquarymod.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Omicron43Reliquary implements ModInitializer {
	public static final String MOD_ID = "reliquarymod";
    public static final Logger LOGGER = LoggerFactory.getLogger("reliquarymod");

	//Additional proofreading by the koolkid94 guy (very cool)
	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

		@Override
	public void onInitialize() {
		LOGGER.info("Omicron43's Reliquary initialized");

		ModEffects.registerEffects();
		ModItems.registerModItems();
		ModItemGroups.registerItemGroups();
		ModDataComponentTypes.registerDataComponentTypes();
	}
}