package org.openmaptiles.addons;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.reader.SourceFeature;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;

public class CultureLayer implements Layer, OpenMapTilesProfile.OsmAllProcessor {

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
        .setAttr("class", "artwork")
        .setAttr("name", feature.getTag("name"))
        .setAttr("inscription", feature.getTag("inscription"))
        .setAttr("start_date", feature.getTag("start_date"))
        .setAttr("artwork_type", feature.getTag("artwork_type"))
        .setAttr("artist_name", feature.getTag("artist_name"))
        .setAttr("artist:wikidata", feature.getTag("artist:wikidata"))
        .setAttr("artist:wikipedia", feature.getTag("artist:wikipedia"))
        .setAttr("wikidata", feature.getTag("wikidata"))
        .setAttr("wikipedia", feature.getTag("wikipedia"))
        .setMinZoom(minZoom);
    }

    if (feature.hasTag("historic", "memorial")) {
      features.point(LAYER_NAME)
        .setAttr("class", "memorial")
        .setAttr("name", feature.getTag("name"))
        .setAttr("subject", feature.getTag("subject"))
        .setAttr("memorial", feature.getTag("memorial"))
        .setAttr("material", feature.getTag("material"))
        .setAttr("start_date", feature.getTag("start_date"))
        .setAttr("wikidata", feature.getTag("wikidata"))
        .setAttr("wikipedia", feature.getTag("wikipedia"))
        .setAttr("architect", feature.getTag("architect"))
        .setAttr("architect:wikidata", feature.getTag("architect:wikidata"))
        .setAttr("architect:wikipedia", feature.getTag("architect:wikipedia"))
        .setAttr("artist_name", feature.getTag("artist_name"))
        .setAttr("artist:wikidata", feature.getTag("artist:wikidata"))
        .setAttr("artist:wikipedia", feature.getTag("artist:wikipedia"))
        .setMinZoom(minZoom);
    }
  }
}
