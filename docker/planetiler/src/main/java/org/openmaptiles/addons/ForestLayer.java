package org.openmaptiles.addons;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.reader.SourceFeature;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;
import org.openmaptiles.util.StreetsUtils;

public class ForestLayer implements Layer, OpenMapTilesProfile.OsmAllProcessor {

  private static final String LAYER_NAME = "forest";
  public int minZoom = 13;

  @Override
  public String name() {
    return LAYER_NAME;
  }

  @Override
  public void processAllOsm(SourceFeature feature, FeatureCollector features) {
    if (feature.canBePolygon() && feature.hasTag("natural", "wood")) {
      features.polygon(LAYER_NAME)
        .setAttr("type", "wood")
        .setAttr("name", feature.getTag("name"))
        .setAttr("leaf_type", StreetsUtils.getLeafType(feature))
        .setMinZoom(minZoom);
    }
  }
}
