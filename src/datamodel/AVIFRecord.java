package datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import parser.ESMJsonParser;

/**
 * Abstraction for AVIF records
 *
 * @author Eric Karlson
 */
public class AVIFRecord extends Record {
    static final String HEALTH_FID = "000002D4";
    static final String SPACESHIP_PART_MASS_FID = "0000ACDB";
    static final String SPACESHIP_CREW_RATING_FID = "00019080";
    static final String SHIP_SYSTEM_WEAPON_HEALTH_FID = "001EC77F";
    static final String SPACESHIP_WEAPON_POWER_FID = "0021961F";

    public AVIFRecord(String formId, String editorId, String signature, JsonNode node, ESMJsonParser parser) {
        super(formId, editorId, signature, node, parser);
    }
}
