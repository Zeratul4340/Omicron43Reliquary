package com.omicron43.omicrons_reliquary.capability;

import com.omicron43.omicrons_reliquary.OmicronsReliquary;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

/*The one who writes NBT*/
public class LaserCapability {
    public static ResourceLocation ID = OmicronsReliquary.id("laser_capability");

    public interface ILaserCapability extends INBTSerializable<CompoundTag> {
        void init(LaserHandler handler);
        void start();
        void tick();
        void stop();
        void end();
    }

    /*public static class */
}
