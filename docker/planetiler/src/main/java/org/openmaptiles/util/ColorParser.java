package org.openmaptiles.util;

// Taken from https://raw.githubusercontent.com/StrandedKitty/planetiler/refs/heads/main/planetiler-examples/src/main/java/com/onthegomap/planetiler/examples/ColorParser.java
// Author: https://github.com/StrandedKitty

import java.util.Map;
import static java.util.Map.entry;
import static org.openmaptiles.util.StreetsUtils.getFirstTagValue;

import com.onthegomap.planetiler.reader.SourceFeature;

public class ColorParser {
  //From https://github.com/openmaptiles/planetiler-openmaptiles/blob/main/src/main/java/org/openmaptiles/layers/Building.java
  private static final Map<String, String> MATERIAL_COLORS = Map.ofEntries(
    entry("cement_block", "#6a7880"),
    entry("brick", "#bd8161"),
    entry("plaster", "#dadbdb"),
    entry("wood", "#d48741"),
    entry("concrete", "#d3c2b0"),
    entry("metal", "#b7b1a6"),
    entry("stone", "#b4a995"),
    entry("mud", "#9d8b75"),
    entry("steel", "#b7b1a6"), // same as metal
    entry("glass", "#5a81a0"),
    entry("traditional", "#bd8161"), // same as brick
    entry("masonry", "#bd8161"), // same as brick
    entry("Brick", "#bd8161"), // same as brick
    entry("tin", "#b7b1a6"), // same as metal
    entry("timber_framing", "#b3b0a9"),
    entry("sandstone", "#b4a995"), // same as stone
    entry("clay", "#9d8b75") // same as mud
  );

  static String parseColor(SourceFeature feature, String initial, String fallback) {
    String color = getFirstTagValue((String) feature.getTag(initial));

    if ((color == null || color.length() == 0) && feature.hasTag(fallback)) {
      String fallbackValue = getFirstTagValue((String) feature.getTag(fallback));
      if (fallbackValue != null && MATERIAL_COLORS.containsKey(fallbackValue)) {
        color = MATERIAL_COLORS.get(fallbackValue);
      }
    }

    if (color == null || color.length() == 0) {
      return null;
    }
    return color;
  }
}
