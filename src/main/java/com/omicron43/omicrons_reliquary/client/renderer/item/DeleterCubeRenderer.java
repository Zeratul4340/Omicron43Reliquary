package com.omicron43.omicrons_reliquary.client.renderer.item;

import com.omicron43.omicrons_reliquary.OmicronsReliquary;
import com.omicron43.omicrons_reliquary.item.relic.DeleterCubeItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DeleterCubeRenderer extends GeoItemRenderer<DeleterCubeItem> {
    public DeleterCubeRenderer() {
        super(new DefaultedItemGeoModel<>(OmicronsReliquary.id("deleter_cube")));
    }
}
