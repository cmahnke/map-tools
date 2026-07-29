package org.openmaptiles.addons;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.reader.SourceFeature;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;

public class Culture implements Layer, OpenMapTilesProfile.OsmAllProcessor {

  private static final String LAYER_NAME = "culture";
  public int minZoom = 14;

  @Override
  public String name() {
    return LAYER_NAME;
  }

  @Override
  public void processAllOsm(SourceFeature feature, FeatureCollector features) {
    if (!feature.isPoint()) {
      return;
    }

    if (feature.hasTag("tourism", "artwork")) {
      features.point(LAYER_NAME)
        .setAttr("class", "artwork") // fixed: was feature.getTag("artwork"), which doesn't exist
        .setAttr("name", feature.getTag("name"))
        .setAttr("artwork_type", feature.getTag("artwork_type"))
        .setAttr("artist_name", feature.getTag("artist_name"))
        .setAttr("wikidata", feature.getTag("wikidata"))
        .setAttr("wikipedia", feature.getTag("wikipedia"))
        .setMinZoom(minZoom);
    }

    if (feature.hasTag("historic", "memorial")) {
      features.point(LAYER_NAME)
        .setAttr("class", "memorial")
        .setAttr("name", feature.getTag("name"))
        .setAttr("memorial", feature.getTag("memorial")) // subtype, may be null
        .setAttr("wikidata", feature.getTag("wikidata"))
        .setAttr("wikipedia", feature.getTag("wikipedia"))
        .setMinZoom(minZoom);
    }
  }
}
