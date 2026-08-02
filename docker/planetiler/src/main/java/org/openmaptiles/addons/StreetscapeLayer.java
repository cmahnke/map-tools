package org.openmaptiles.addons;

import com.onthegomap.planetiler.FeatureCollector;
import com.onthegomap.planetiler.reader.SourceFeature;
import org.openmaptiles.Layer;
import org.openmaptiles.OpenMapTilesProfile;
import org.openmaptiles.util.StreetsUtils;

public class StreetscapeLayer implements Layer, OpenMapTilesProfile.OsmAllProcessor {

  private static final String LAYER_NAME = "streetscape";

  @Override
  public String name() {
    return LAYER_NAME;
  }

  @Override
  public void processAllOsm(SourceFeature feature, FeatureCollector features) {
    if (!feature.canBeLine() || !feature.hasTag("highway")) {
      return;
    }

    String highwayValue = feature.getTag("highway").toString();
    boolean isConstruction = "construction".equals(highwayValue);
    String effectiveHighway = isConstruction && feature.hasTag("construction")
      ? feature.getTag("construction").toString()
      : highwayValue;

    boolean isBridge = feature.hasTag("bridge") && !feature.hasTag("bridge", "no");
    boolean isTunnel = feature.hasTag("tunnel") && !feature.hasTag("tunnel", "no");
    boolean isFord   = feature.hasTag("ford")   && !feature.hasTag("ford", "no");
    String brunnel = isBridge ? "bridge" : isTunnel ? "tunnel" : isFord ? "ford" : null;

    features.line(LAYER_NAME)
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

      // Access / Permissions
      .setAttr("bicycle", feature.getTag("bicycle"))
      .setAttr("foot", feature.getTag("foot"))
      .setAttr("vehicle", feature.getTag("vehicle"))
      .setAttr("motorVehicle", feature.getTag("motor_vehicle"))
      .setAttr("moped", feature.getTag("moped"))
      .setAttr("smallElectricVehicle", feature.getTag("small_electric_vehicle"))
      .setAttr("onewayBicycle", feature.getTag("oneway:bicycle"))

      // Bicycle-specific
      .setAttr("cycleway", feature.getTag("cycleway"))
      .setAttr("segregated", feature.getTag("segregated"))
      .setAttr("bicycleRoad", feature.getTag("bicycle_road"))

      // Pedestrian-specific
      .setAttr("footway", feature.getTag("footway"))
      .setAttr("crossing", feature.getTag("crossing"))
      .setAttr("wheelchair", feature.getTag("wheelchair"))
      .setAttr("tactilePaving", feature.getTag("tactile_paving"))
      .setAttr("kerb", feature.getTag("kerb"))

      // Quality / Condition
      .setAttr("smoothness", feature.getTag("smoothness"))
      .setAttr("incline", feature.getTag("incline"))
      .setAttr("lit", feature.getTag("lit"))

      .setMinZoom(StreetsUtils.getHighwayMinZoom(effectiveHighway));
  }
}
