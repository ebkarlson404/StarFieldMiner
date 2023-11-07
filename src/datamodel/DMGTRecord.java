package datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import parser.ESMJsonParser;

/**
 * Abstraction of the DMGT records
 *
 * @author Eric Karlson
 */
public class DMGTRecord extends Record {
    static final String SHIELD_FID = "0001EDE8";
    static final String ELECTROMAGNETIC_FID = "00023190";

    public DMGTRecord(String formId, String editorId, String signature, JsonNode node, ESMJsonParser parser) {
        super(formId, editorId, signature, node, parser);
    }
}
