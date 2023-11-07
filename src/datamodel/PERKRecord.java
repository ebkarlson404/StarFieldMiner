package datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import parser.ESMJsonParser;

/**
 * Abstraction of the PERK records
 *
 * @author Eric Karlson
 */
@ESMGroup("PERK")
public class PERKRecord extends Record {
    static final String SKILL_STARSHIP_DESIGN_FID = "002C59DC";

    public PERKRecord(String formId, String editorId, String signature, JsonNode node, ESMJsonParser parser) {
        super(formId, editorId, signature, node, parser);
    }
}
