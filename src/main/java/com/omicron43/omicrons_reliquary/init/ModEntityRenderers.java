package com.omicron43.omicrons_reliquary.init;

import com.omicron43.omicrons_reliquary.client.renderer.entity.LaserEntityRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;

/*we are not declaring ts in the main mod class*/
public class ModEntityRenderers {

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.LASER.get(), LaserEntityRenderer::new);
    }
}
