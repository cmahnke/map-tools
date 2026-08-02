package org.openmaptiles.addons;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.reader.SourceFeature;
import org.openmaptiles.Layer;
import com.onthegomap.planetiler.util.Parse;
import org.openmaptiles.util.StreetsUtils;
import org.openmaptiles.OpenMapTilesProfile;

import java.util.List;

public class Projektemacher implements Layer, OpenMapTilesProfile.OsmAllProcessor {

  private static final String LAYER_NAME = "projektemacher";
  public int minZoom = 13;

  @Override
  public String name() {
    return LAYER_NAME;
  }

  @Override
  public void processAllOsm(SourceFeature feature, FeatureCollector features) {
    if (feature.canBePolygon()) {
      if (
        (
          feature.hasTag("building:part") &&
            !feature.getTag("building:part").equals("no")
        ) || (
          feature.hasTag("building") &&
            !feature.getTag("building").equals("no")
        )
      ) {
        Boolean isPart = feature.hasTag("building:part");
        //String buildingType = isPart ? (String) feature.getTag("building:part") : (String) feature.getTag("building");

        features.polygon("projektemacher-building")
          .setAttr("type", "building")
          .setAttr("isPart", isPart)
          .setAttr("highlight", feature.getTag("highlight"))
          //TODO: This might also be part of the relation conneting building parts
          .setAttr("architect", feature.getTag("architect"))
          .setAttr("architect:wikidata", feature.getTag("architect:wikidata"))
          .setAttr("architecture", feature.getTag("architecture"))
          .setAttr("construction_date", feature.getTag("construction_date"))
          .setAttr("year_of_construction", feature.getTag("year_of_construction"))
          .setAttr("start_date", feature.getTag("start_date"))
          .setAttr("wikidata", feature.getTag("wikidata"))
          .setAttr("wikipedia", feature.getTag("wikipedia"))
          .setAttr("meta", feature.getTag("meta"))
          //.setAttr("buildingType", buildingType)
          .setAttr("name", feature.getTag("name"))
          .setAttr("height", StreetsUtils.getHeight(feature))
          .setAttr("minHeight", StreetsUtils.getMinHeight(feature))
          .setAttr("render_height", StreetsUtils.getHeight(feature))
          .setAttr("render_min_height", StreetsUtils.getMinHeight(feature))
          .setAttr("levels", StreetsUtils.getBuildingLevels(feature))
          .setAttr("minLevel", StreetsUtils.getBuildingMinLevel(feature))
          .setAttr("material", StreetsUtils.getBuildingMaterial(feature))
          .setAttr("roofHeight", StreetsUtils.getRoofHeight(feature))
          .setAttr("roofLevels", StreetsUtils.getRoofLevels(feature))
          .setAttr("roofMaterial", StreetsUtils.getRoofMaterial(feature))
          .setAttr("roofType", StreetsUtils.getRoofShape(feature))
          .setAttr("roofOrientation", StreetsUtils.getRoofOrientation(feature))
          .setAttr("roofDirection", StreetsUtils.getRoofDirection(feature))
          .setAttr("roofAngle", StreetsUtils.getAngle(feature))
          .setAttr("roofColor", StreetsUtils.getRoofColor(feature))
          .setAttr("color", StreetsUtils.getBuildingColor(feature))
          .setAttr("windows", StreetsUtils.getBuildingWindows(feature))
          .setAttr("defaultRoof", StreetsUtils.getBuildingDefaultRoof(feature))
          .setMinZoom(minZoom);
      }

      if (feature.hasTag("natural", "wood")) {
        features.polygon("forest")
          .setAttr("type", "wood")
          .setAttr("name", feature.getTag("name"))
          .setAttr("leaf_type", feature.getTag("leaf_type"))
          .setMinZoom(minZoom);
      }
    }
  }

}
