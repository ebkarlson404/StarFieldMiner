package datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import parser.ESMJsonParser;

/**
 * Abstracts a KYWD record in an ESM JSON Dump
 *
 * @author Eric Karlson
 */
@ESMGroup("KYWD")
public class KYWDRecord extends Record {
    private static final String FLD_KEYWORD_TYPE = "TNAM - Type";
    private static final String KEYWORD_TYPE_SHIP_MODULE_CLASS = "Ship Module Class";
    private static final String KEYWORD_TYPE_MANUFACTURER = "Unknown 31";
    public static final String CATEGORY_SHIPMOD_WEAPON_FID = "002C155B";
    public static final String SPACESHIP_PART_LINKED_WEAPON_FID = "000179DD";
    public static final String SPACESHIP_ENERGY_WEAPON_FID = "0002226A";
    public static final String SPACESHIP_PARTICLE_WEAPON_FID = "001557AA";
    public static final String SPACESHIP_ELECTROMAGNETIC_WEAPON_FID = "0002226B";
    public static final String SPACESHIP_MISSILE_WEAPON_FID = "00155C6C";
    public static final String SPACESHIP_KINETIC_WEAPON_FID = "00022269";
    public static final String SPACESHIP_TURRET_WEAPON_FID = "0032792C";

    public KYWDRecord(String formId, String editorId, String signature, JsonNode node, ESMJsonParser parser) {
        super(formId, editorId, signature, node, parser);
    }

    /**
     * @return {@code true} if this KEYW is used to convey a ship's class (i.e. A, B, C ...)
     */
    public boolean isShipModuleClassKeyword() {
        return KEYWORD_TYPE_SHIP_MODULE_CLASS.equals(getPropertyAsString(FLD_KEYWORD_TYPE));
    }

    /**
     * @return {#code true} if this KEYW is used to convey the name of a ship module manufacturer
     */
    public boolean isShipModuleCorpNameKeyword() {
        return KEYWORD_TYPE_MANUFACTURER.equals(getPropertyAsString(FLD_KEYWORD_TYPE));
    }
}
