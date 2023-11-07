package datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import parser.ESMJsonParser;

/**
 * Abstraction for AVIF records
 *
 * @author Eric Karlson
 */
public class AVIFRecord extends Record {
  public static final String HEALTH_FID = "000002D4";
  public static final String SPACESHIP_PART_MASS_FID = "0000ACDB";
  public static final String SPACESHIP_CREW_RATING_FID = "00019080";
  public static final String SHIP_SYSTEM_WEAPON_HEALTH_FID = "001EC77F";
  public static final String SPACESHIP_WEAPON_POWER_FID = "0021961F";

  public AVIFRecord(
      @NotNull String formId,
      @NotNull String editorId,
      @NotNull String signature,
      @NotNull JsonNode node,
      ESMJsonParser.@NotNull ParserRegistrar registrar) {
    super(formId, editorId, signature, node, registrar);
  }
}
