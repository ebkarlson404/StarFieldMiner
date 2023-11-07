package parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import util.ESMKeyValueMap;

import java.util.Map;

/**
 * Overrides the standard {@link ObjectNode} class with a variant that will track repeated occurrences
 * of the same property key rather than tracking just the last occurrence.  Addresses the "repeated key"
 * issue that arises from the "serialize-command-json" xEdit script.
 *
 * @author Eric Karlson
 */
public class ESMObjectNode extends ObjectNode {

    public ESMObjectNode(JsonNodeFactory nc) {
        // Override the internal HashMap to use our custom ESMKeyValueMap which handles
        // insertions of duplicate property keys
        super(nc, new ESMKeyValueMap());
    }

    public ESMObjectNode(JsonNodeFactory nc, Map<String, JsonNode> kids) {
        // Override the internal HashMap to use our custom ESMKeyValueMap which handles
        // insertions of duplicate property keys
        super(nc, new ESMKeyValueMap(kids));
    }
}
