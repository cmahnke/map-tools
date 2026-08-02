package org.openmaptiles.addons;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.reader.SourceFeature;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;
import org.openmaptiles.util.StreetsUtils;

public class TreeRowLayer implements Layer, OpenMapTilesProfile.OsmAllProcessor {

  private static final String LAYER_NAME = "tree_row";
  public int minZoom = 13;

  @Override
  public String name() {
    return LAYER_NAME;
  }

  @Override
  public void processAllOsm(SourceFeature feature, FeatureCollector features) {
    if (feature.canBeLine() && feature.hasTag("natural", "tree_row")) {
      features.line(LAYER_NAME)
        .setAttr("height", StreetsUtils.getTreeHeight(feature))
        .setAttr("genus", StreetsUtils.getGenus(feature))
        .setAttr("leaf_type", StreetsUtils.getLeafType(feature))
        .setMinZoom(minZoom);
    }
  }
}
