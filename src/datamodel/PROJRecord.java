package datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.Nullable;
import parser.ESMJsonParser;
import parser.Util;

/**
 * Abstraction of the PROJ record type
 *
 * @author Eric Karlson
 */
@ESMGroup("PROJ")
public class PROJRecord extends Record {
    private static final String FLD_PROD = "PROD - Data";
    private static final String FLD_SPEED = "Speed";
    private static final String FLD_EXPLOSION = "Explosion";

    public PROJRecord(String formId, String editorId, String signature, JsonNode node, ESMJsonParser parser) {
        super(formId, editorId, signature, node, parser);
    }

    public int getSpeed() {
        JsonNode prod = node.get(FLD_PROD);
        if (null == prod) {
            return 0;
        }
        JsonNode speed = prod.get(FLD_SPEED);
        return Util.asInt(speed, 0);
    }

    public @Nullable EXPLRecord getEXPLRecord() {
        JsonNode prod = node.get(FLD_PROD);
        if (null == prod) {
            return null;
        }
        JsonNode expl = prod.get(FLD_EXPLOSION);
        return (null != expl && expl.isTextual()) ? parser.findRecordByFormId(expl.asText(), EXPLRecord.class) : null;
    }
}
