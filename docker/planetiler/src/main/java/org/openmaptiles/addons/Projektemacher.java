package org.openmaptiles.addons;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.reader.SourceFeature;
import org.openmaptiles.Layer;
import com.onthegomap.planetiler.util.Parse;
import org.openmaptiles.util.StreetsUtils;
import org.openmaptiles.OpenMapTilesProfile;

public class Projektemacher implements Layer, OpenMapTilesProfile.OsmAllProcessor {

  private static final String LAYER_NAME = "projektemacher";

  @Override
  public String name() {
    return LAYER_NAME;
  }

  @Override
  public void processAllOsm(SourceFeature feature, FeatureCollector features) {
    if (feature.isPoint()) {
      if (feature.hasTag("natural", "tree")) {
        Double height = StreetsUtils.getTreeHeight(feature);
        //Double height = Parse.meters(feature.getTag("height"));
        features.point("tree")
          .setAttr("height", height)
          .setMinZoom(14);

      }

      if (feature.hasTag("tourism", "artwork")) {
        features.point("artwork")
          .setAttr("name", feature.getTag("name"))
          .setAttr("artwork_type", feature.getTag("artwork_type"))
          .setMinZoom(14);
      }
    }
    if (feature.canBeLine()) {
      if (feature.hasTag("natural", "tree_row")) {
        features.line("tree")
          .setAttr("height", StreetsUtils.getTreeHeight(feature))
          .setMinZoom(14);
      }
    }

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

        features.polygon("building")
          .setAttr("type", "building")
          .setAttr("isPart", isPart)
          .setAttr("highlight", feature.getTag("highlight"))
          //TODO: This might also be part of the relation conneting building parts
          .setAttr("architect", feature.getTag("architect"))
          .setAttr("architecture", feature.getTag("architecture"))
          .setAttr("construction_date", feature.getTag("construction_date"))
          .setAttr("wikidata", feature.getTag("wikidata"))
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
          .setAttr("defaultRoof", StreetsUtils.getBuildingDefaultRoof(feature));
      }
    }
  }

}
