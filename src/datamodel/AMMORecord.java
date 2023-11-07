package datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import parser.ESMJsonParser;

/**
 * Abstraction of the AMMO record type
 *
 * @author Eric Karlson
 */
@ESMGroup("AMMO")
public class AMMORecord extends Record {
    private static final String FLD_DNAM = "DNAM - DNAM";
    private static final String FLD_PROJECTILE = "Projectile";

    public AMMORecord(String formId, String editorId, String signature, JsonNode node, ESMJsonParser parser) {
        super(formId, editorId, signature, node, parser);
    }

    public PROJRecord getPROJRecord() {
        JsonNode dnam = node.get(FLD_DNAM);
        if (null == dnam) {
            return null;
        }
        JsonNode proj = dnam.get(FLD_PROJECTILE);
        return (null != proj && proj.isTextual()) ? parser.findRecordByFormId(proj.asText(), PROJRecord.class) : null;
    }
}
