package util;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A custom override of a standard {@link HashMap} that alters the behavior when inserting a key
 * that already exists in the map.  Rather than replacing the existing value, it fabricates a new
 * key value for the value being added.  This addresses the duplicate property name issue that
 * arises from the existing "serialize-command-json" script used with xEdit.
 *
 * @author Eric Karlson
 */
public class ESMKeyValueMap extends HashMap<String, JsonNode> {
    /**
     * Tracks duplicated keys in the object
     */
    private final Map<String, Integer> duplicateKeys = new HashMap<>();

    public ESMKeyValueMap() {
        super();
    }

    public ESMKeyValueMap(Map<String, JsonNode> source) {
        super();
        this.putAll(source);
    }

    /**
     * Disambiguates repeated occurrences of a property key in a JSON Object by decorating the repeated
     * property name with " #<ver>"
     *
     * @param propName  The property name that is repeated in the JSON Object
     * @param iteration The occurrence of the property name in question (1-based)
     * @return A unique property key for this occurrence of the repeated key
     */
    public static @NotNull String generateRepeatedKey(@NotNull String propName, int iteration) {
        Assert.assertTrue(iteration > 0, "Invalid 'iteration' value - should be 1-based, not 0-based");
        return (1 == iteration) ? propName : String.format("%s #%d", propName, iteration);
    }

    @Override
    public JsonNode put(String key, JsonNode value) {
        // How many times have we seen this key?
        Integer cnt = 1 + duplicateKeys.computeIfAbsent(key, k -> 0);
        duplicateKeys.put(key, cnt);

        // Disambiguate the key value (if needed) and store the value
        return super.put(generateRepeatedKey(key, cnt), value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends JsonNode> m) {
        m.entrySet().iterator().forEachRemaining(e -> put(e.getKey(), e.getValue()));
    }
}
