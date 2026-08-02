package org.openmaptiles.addons;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.reader.SourceFeature;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;
import org.openmaptiles.util.StreetsUtils;

public class TreeLayer implements Layer, OpenMapTilesProfile.OsmAllProcessor {

  private static final String LAYER_NAME = "tree";
  public int minZoom = 13;

  @Override
  public String name() {
    return LAYER_NAME;
  }

  @Override
  public void processAllOsm(SourceFeature feature, FeatureCollector features) {
    if (feature.isPoint() && feature.hasTag("natural", "tree")) {
      Double height = StreetsUtils.getTreeHeight(feature);
      features.point(LAYER_NAME)
        .setAttr("genus", feature.getTag("genus"))
        .setAttr("height", height)
        .setMinZoom(minZoom);
    }
  }
}
