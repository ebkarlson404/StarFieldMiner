package parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import datamodel.ESMGroup;
import datamodel.Record;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Assert;
import util.Holder;

/**
 * Top-level parser for processing an ESM Dump File.
 *
 * @author Eric Karlson
 */
public class ESMJsonParser {

  /** Maps FormID's to {@link Record}'s */
  private final Map<String, Record> recordsByFormId = new HashMap<>();

  /** Maps Editor ID's to {@link Record}'s */
  private final Map<String, Record> recordsByEditorId = new HashMap<>();

  /** Maps Record Signature to all {@link Record}'s that have that signature */
  private final Map<String, List<Record>> recordsBySignature = new HashMap<>();

  /**
   * A callback class passed to the {@link Record}'s constructor to register the new {@link Record}
   * with the {@link ESMJsonParser} that created it *
   */
  public static class ParserRegistrar {
    private final ESMJsonParser parser;

    private ParserRegistrar(@NotNull ESMJsonParser parser) {
      this.parser = parser;
    }

    public void register(@NotNull Record record) {
      parser.registerRecord(record);
    }

    public ESMJsonParser getParser() {
      return parser;
    }
  }

  /**
   * Construct a parser for a given ESM JSON Dump File. The parser maintains internal state to track
   * all records discovered, cumulatively, by each call to {@link #parse(String)}.
   *
   * @see #findRecordByEditorId(String, Class)
   * @see #findRecordByFormId(String, Class)
   * @see #getGroup(Class)
   * @see #parse(String)
   */
  public ESMJsonParser() {}

  /**
   * Parses the indicated ESM Json Dump File and adds all discovered records into this parser's data
   * model.
   *
   * @param filename The name of the ESM Json Dump File to read
   * @throws IOException If there is some problem reading the file
   */
  public void parse(@NotNull String filename, @NotNull Holder<String> current) throws IOException {
    current.set(filename);

    // Customized JsonNodeFactory that replaces JsonObject with our specialized ESMObjectNode
    // which handles Json Objects with repeated property values (something that happens with
    // xEdit and the serialize-command-json scripts)
    ObjectMapper mapper = new ObjectMapper();
    mapper.setNodeFactory(new ESMNodeFactory());

    // Customized CharsetDecoder which will ignore invalid character encodings
    Charset charset = Charset.forName("cp1252");
    CharsetDecoder decoder = charset.newDecoder();
    decoder.onMalformedInput(CodingErrorAction.IGNORE);

    // The registrar to use for Record self-registration
    ParserRegistrar registrar = new ParserRegistrar(this);

    // Now parse the dump file, ignoring malformed UTF-8
    try (FileInputStream fis = new FileInputStream(filename);
        InputStreamReader isr = new InputStreamReader(fis, decoder);
        BufferedReader br = new BufferedReader(isr)) {
      JsonNode dom = mapper.readValue(br, JsonNode.class);

      // Traverse the resulting DOM and generate/register Record Objects for all discovered records
      Assert.assertTrue(dom.isObject(), "ESM Dump File must be an OBJECT of record objects");
      dom.elements().forEachRemaining(r -> RecordFactory.fromESMJsonObject(r, registrar));
    }
  }

  /**
   * Called by the {@link Record} constructor to register the new record with this parser
   *
   * @param rec The new {@link Record} to register
   */
  private void registerRecord(@NotNull Record rec) {
    recordsByFormId.put(rec.getFormId(), rec);
    recordsByEditorId.put(rec.getEditorId(), rec);
    List<Record> records =
        recordsBySignature.computeIfAbsent(rec.getSignature(), k -> new ArrayList<>());
    records.add(rec);
  }

  /**
   * Find all {@link Record}'s that of a particular type
   *
   * @param clazz The {@link Class} of {@link Record} objects that we want
   * @param <T> The type of {@link Record} that we are interested in
   * @return A list of all {@link Record}'s of the indicated type
   */
  public <T extends Record> @NotNull List<Record> getGroup(@NotNull Class<T> clazz) {
    ESMGroup group =
        Assert.assertNotNull(
            clazz.getAnnotation(ESMGroup.class), "class must have an @ESMGroup annotation");
    return recordsBySignature.computeIfAbsent(group.value(), g -> Collections.emptyList());
  }

  /**
   * Finds a {@link Record} by its FormID
   *
   * @param formId The FormID for the record of interest
   * @param clazz The expected {@link Class} of the record, or {@code Record.class} for any type of
   *     record
   * @param <T> The expected class of the record
   * @return The {@link Record} with the indicated FormID, or {@code null} if there is no such
   *     record
   */
  public <T extends Record> @Nullable T findRecordByFormId(
      @NotNull String formId, @NotNull Class<T> clazz) {
    return Util.cast(recordsByFormId.get(formId), clazz);
  }

  /**
   * Finds a {@link Record} by its EditorID
   *
   * @param editorId The EditorID for the record of interest
   * @param clazz The expected {@link Class} of the record, or {@code Record.class} for any type of
   *     record
   * @param <T> The expected class of the record
   * @return The {@link Record} with the indicated FormID, or {@code null} if there is no such
   *     record
   */
  public <T extends Record> @Nullable T findRecordByEditorId(
      @NotNull String editorId, @NotNull Class<T> clazz) {
    return Util.cast(recordsByEditorId.get(editorId), clazz);
  }
}
