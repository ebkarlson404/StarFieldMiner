package parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import datamodel.ESMGroup;
import datamodel.Record;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Assert;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Top-level parser for processing an ESM Dump File.
 * <p>The ESM Dump File is created by adding the <i>serialize-command-json</i> script to your
 * xEdit deployment, and then using it to export all of the following types of records
 * into a single *.json file:
 * <ul>
 *     <li>AMMO - <i>Ammunition</i> in Starfield.esm</li>
 *     <li>AVIF - <i>Actor Value Information</i> in Starfield.esm and Starfield.exe</li>
 *     <li>COBJ - <i>Constructible Object</i> in Starfield.esm</li>
 *     <li>DMGT - <i>Damage Type</i> in Starfield.esm</li>
 *     <li>EXPL - <i>Explosion</i> in Starfield.esm</li>
 *     <li>GBFM - <i>Generic Base Form</i> in Starfield.esm</li>
 *     <li>KEYW = <i>Keyword</i> in Starfield.esm</li>
 *     <li>PERK - <i>Perk</i> in Starfield.esm</li>
 *     <li>PROJ - <i>Projectile</i> in Starfield.esm</li>
 *     <li>WEAP - <i>Weapon</i> in Starfield.esm</li>
 * </ul></p>
 * <p>One can export all of these into a single file by selecting all of the group using
 * ctrl-left-click and then right-click on one of the selected items, choose <i>Apply Script...</i>
 * and then using the <i>serialize-command-json</i> script.  The JSON dump will appear in the
 * same directory where xEdit is located with the name <i>output.json</i>.
 *
 * @author Eric Karlson
 */
public class ESMJsonParser {

    /**
     * Maps FormID's to {@link Record}'s
     */
    private final Map<String, Record> recordsByFormId = new HashMap<>();

    /**
     * Maps Editor ID's to {@link Record}'s
     */
    private final Map<String, Record> recordsByEditorId = new HashMap<>();

    /**
     * Maps Record Signature to all {@link Record}'s that have that signature
     */
    private final Map<String, List<Record>> recordsBySignature = new HashMap<>();

    /**
     * Construct a parser for a given ESM JSON Dump File.  The parser maintains internal
     * state to track all records discovered, cummulatiely, bu each call to {@link #parse(String)}.
     *
     * @see #findRecordByEditorId(String, Class)
     * @see #findRecordByFormId(String, Class)
     * @see #getGroup(Class)
     * @see #parse(String)
     */
    public ESMJsonParser() {
    }

    /**
     * Parses the indicated ESM Json Dump File and adds all discovered records into this parser's
     * data model.
     *
     * @param filename The name of the ESM Json Dump File to read
     * @throws IOException
     */
    public void parse(@NotNull String filename) throws IOException {
        // Customized JsonNodeFactory that replaces JsonObject with our specialized ESMObjectNode
        // which handles Json Objects with repeated property values (something that happens with
        // xEdit and the serialize-command-json scripts)
        ObjectMapper mapper = new ObjectMapper();
        mapper.setNodeFactory(new ESMNodeFactory());

        // Customized CharsetDecoder which will replace unrecognized UTF-8 sequences with '?'.
        // Required as there is some string data in the ESM dumps that is not valid UTF-8.
        // Perhaps it is some other charset, but I've not been able to determine a charset
        // that will cleanly process the DSM dump data, so instead I ignoring invalid UTF-8
        // sequences
        Charset charset = StandardCharsets.UTF_8;
        CharsetDecoder decoder = charset.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.IGNORE);

        // Now parse the dump file, ignoring malformed UTF-8
        try (FileInputStream fis = new FileInputStream(filename);
             InputStreamReader isr = new InputStreamReader(fis, decoder);
             BufferedReader br = new BufferedReader(isr)) {
            JsonNode dom = mapper.readValue(br, JsonNode.class);

            // Traverse the resulting DOM and generate/register Record's for everything
            Assert.assertTrue(dom.isObject(), "ESM Dump File must be an OBJECT of record objects");
            dom.elements().forEachRemaining(r -> {
                RecordFactory.fromESMJsonObject(r, this);
            });
        }
    }

    /**
     * Called by the {@link Record} constructor to register the new record with this parser
     *
     * @param rec The new {@link Record} to register
     */
    public void registerRecord(@NotNull Record rec) {
        recordsByFormId.put(rec.getFormId(), rec);
        recordsByEditorId.put(rec.getEditorId(), rec);
        List<Record> records = recordsBySignature.computeIfAbsent(rec.getSignature(), k -> new ArrayList<>());
        records.add(rec);
    }

    /**
     * Find all {@link Record}'s that of a particular type
     *
     * @param clazz The {@link Class} of {@link Record} objects that we want
     * @param <T>   The type of {@link Record} that we are interested in
     * @return A list of all {@Record}'s of the indicated type
     */
    public <T extends Record> @NotNull List<Record> getGroup(@NotNull Class<T> clazz) {
        ESMGroup group = Assert.assertNotNull(
                clazz.getAnnotation(ESMGroup.class), "class must have an @ESMGroup annotation");
        return recordsBySignature.computeIfAbsent(group.value(), g -> Collections.EMPTY_LIST);
    }

    /**
     * Finds a {@link Record} by its FormID
     *
     * @param formId The FormID for the record of interest
     * @param clazz  The expected {@link Class} of the record, or {@code Record.class} for any type of record
     * @param <T>    The expected class of the record
     * @return The {@link Record} with the indicated FormID, or {@code null} if there is no such record
     */
    public <T extends Record> @Nullable T findRecordByFormId(@NotNull String formId, @NotNull Class<T> clazz) {
        Record rec = recordsByFormId.get(formId);
        return Util.cast(recordsByFormId.get(formId), clazz);
    }

    /**
     * Finds a {@link Record} by its EditorID
     *
     * @param editorId The EditorID for the record of interest
     * @param clazz    The expected {@link Class} of the record, or {@code Record.class} for any type of record
     * @param <T>      The expected class of the record
     * @return The {@link Record} with the indicated FormID, or {@code null} if there is no such record
     */
    public <T extends Record> @Nullable T findRecordByEditorId(@NotNull String editorId, @NotNull Class<T> clazz) {
        return Util.cast(recordsByEditorId.get(editorId), clazz);
    }
}
