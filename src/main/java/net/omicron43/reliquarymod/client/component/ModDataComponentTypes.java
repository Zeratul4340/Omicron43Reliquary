package net.omicron43.reliquarymod.client.component;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.Vec3d;
import net.omicron43.reliquarymod.Omicron43Reliquary;

import java.util.function.UnaryOperator;

public class ModDataComponentTypes {
    public static final ComponentType<Vec3d> RAYCAST_COORDINATES =
            register("raycast_coordinates", vec3dBuilder -> vec3dBuilder.codec(Vec3d.CODEC));


    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Omicron43Reliquary.id(name),
                builderOperator.apply(ComponentType.builder()).build());
    }

    public static void registerDataComponentTypes() {
        Omicron43Reliquary.LOGGER.info("Registering Data Component Types for " + Omicron43Reliquary.MOD_ID);
    }
}
