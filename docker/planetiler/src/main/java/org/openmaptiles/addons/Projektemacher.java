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
    if (feature.isPoint()) {
      if (feature.hasTag("natural", "tree")) {
        Double height = StreetsUtils.getTreeHeight(feature);
        features.point("tree")
          .setAttr("genus", feature.getTag("genus"))
          .setAttr("height", height)
          .setMinZoom(minZoom);
      }
    }

    if (feature.canBeLine()) {

      if (feature.hasTag("natural", "tree_row")) {
        Double height = StreetsUtils.getTreeHeight(feature);
        //Double height = Parse.meters(feature.getTag("height"));
        features.line("tree_row")
          .setAttr("height", height)
          .setMinZoom(minZoom);
      }

      if (feature.hasTag("barrier", "wall")) {
        Double height = StreetsUtils.getWallHeight(feature);
        //Double height = Parse.meters(feature.getTag("height"));
        features.line("barrier")
          .setAttr("height", height)
          .setMinZoom(minZoom);
      }

      if (feature.hasTag("highway")) {
        String highwayValue = feature.getTag("highway").toString();
        boolean isConstruction = "construction".equals(highwayValue);
        String effectiveHighway = isConstruction && feature.hasTag("construction")
          ? feature.getTag("construction").toString()
          : highwayValue;

        boolean isBridge = feature.hasTag("bridge") && !feature.hasTag("bridge", "no");
        boolean isTunnel = feature.hasTag("tunnel") && !feature.hasTag("tunnel", "no");
        boolean isFord   = feature.hasTag("ford")   && !feature.hasTag("ford", "no");
        String brunnel = isBridge ? "bridge" : isTunnel ? "tunnel" : isFord ? "ford" : null;

        features.line("streetscape")
          .setAttr("type", "path")
          .setAttr("pathType", effectiveHighway)
          .setAttr("surface", StreetsUtils.getSurface(feature))
          .setAttr("width", StreetsUtils.getWidth(feature))
          .setAttr("laneMarkings", StreetsUtils.getLaneMarkings(feature))
          .setAttr("sidewalkSide", StreetsUtils.convertRoadwayExtensionSideToInteger(StreetsUtils.getSidewalkSide(feature)))
          .setAttr("cyclewaySide", StreetsUtils.convertRoadwayExtensionSideToInteger(StreetsUtils.getCyclewaySide(feature)))
          .setAttr("oneway", StreetsUtils.isRoadwayOneway(feature))
          .setAttr("ref", feature.getTag("ref"))
          .setAttr("service", feature.getTag("service"))
          .setAttr("access", feature.getTag("access"))
          .setAttr("brunnel", brunnel)

          // --- Zugang / Erlaubnis ---
          .setAttr("bicycle", feature.getTag("bicycle"))
          .setAttr("foot", feature.getTag("foot"))
          .setAttr("vehicle", feature.getTag("vehicle"))
          .setAttr("motorVehicle", feature.getTag("motor_vehicle"))
          .setAttr("moped", feature.getTag("moped"))
          .setAttr("smallElectricVehicle", feature.getTag("small_electric_vehicle"))
          .setAttr("onewayBicycle", feature.getTag("oneway:bicycle"))

          // --- Rad-spezifisch ---
          .setAttr("cycleway", feature.getTag("cycleway"))
          .setAttr("segregated", feature.getTag("segregated"))
          .setAttr("bicycleRoad", feature.getTag("bicycle_road"))

          // --- Fußgänger-spezifisch ---
          .setAttr("footway", feature.getTag("footway"))
          .setAttr("crossing", feature.getTag("crossing"))
          .setAttr("wheelchair", feature.getTag("wheelchair"))
          .setAttr("tactilePaving", feature.getTag("tactile_paving"))
          .setAttr("kerb", feature.getTag("kerb"))

          // --- Komfort / Zustand ---
          .setAttr("smoothness", feature.getTag("smoothness"))
          .setAttr("incline", feature.getTag("incline"))
          .setAttr("lit", feature.getTag("lit"))

          .setMinZoom(StreetsUtils.getHighwayMinZoom(effectiveHighway));
      }

      /*
      if (StreetsUtils.isRailway(feature)) {
        features.line("railway")
          .setAttr("type", "railway")
          .setAttr("railwayType", StreetsUtils.getRailwayType(feature))
          .setAttr("gauge", StreetsUtils.getGauge(feature));

      }
      */
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
