package com.omicron43.reliquarymod;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Omicron43Reliquary implements ModInitializer {
	public static final String MOD_ID = "reliquarymod";
    public static final Logger LOGGER = LoggerFactory.getLogger("reliquarymod");

	@Override
	public void onInitialize() {
		LOGGER.info("Omicron43's Reliqaury initialized");
		LOGGER.info("BTW Vel'koz #1");
	}
}