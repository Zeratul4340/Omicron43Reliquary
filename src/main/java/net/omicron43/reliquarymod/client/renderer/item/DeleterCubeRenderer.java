package net.omicron43.reliquarymod.client.renderer.item;

import net.omicron43.reliquarymod.Omicron43Reliquary;
import net.omicron43.reliquarymod.item.custom.DeleterCubeItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DeleterCubeRenderer extends GeoItemRenderer<DeleterCubeItem> {
    public DeleterCubeRenderer(){
        super(new DefaultedItemGeoModel<>(Omicron43Reliquary.id("deleter_cube")));
    }
}
