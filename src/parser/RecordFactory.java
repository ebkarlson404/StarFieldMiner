package parser;

import com.fasterxml.jackson.databind.JsonNode;
import datamodel.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import util.Assert;

/**
 * Factory class to create instances of {@link Record} based on the contents of the JSON <i>Record
 * Header</i>
 *
 * @author Eric Karlson
 */
public class RecordFactory {
  private static final Map<String, Class<?>> SIGNATURE_TO_CLASS = new HashMap<>();

  static {
    SIGNATURE_TO_CLASS.put("AMMO", AMMORecord.class);
    SIGNATURE_TO_CLASS.put("AVIF", AVIFRecord.class);
    SIGNATURE_TO_CLASS.put("COBJ", COBJRecord.class);
    SIGNATURE_TO_CLASS.put("DMGT", DMGTRecord.class);
    SIGNATURE_TO_CLASS.put("EXPL", EXPLRecord.class);
    SIGNATURE_TO_CLASS.put("FLST", FLSTRecord.class);
    SIGNATURE_TO_CLASS.put("GBFM", GBFMRecord.class);
    SIGNATURE_TO_CLASS.put("GLOB", GLOBRecord.class);
    SIGNATURE_TO_CLASS.put("KYWD", KYWDRecord.class);
    SIGNATURE_TO_CLASS.put("PERK", PERKRecord.class);
    SIGNATURE_TO_CLASS.put("PROJ", PROJRecord.class);
    SIGNATURE_TO_CLASS.put("WEAP", WEAPRecord.class);
  }

  /**
   * The factory builder method. Note that the caller is responsible for registering the new {@link
   * Record} with some {@link ESMJsonParser} to make it available for data minding.
   *
   * @param formId The Form ID that uniquely identifies this record
   * @param editorId The Editor ID for this record
   * @param signature The SIgnaure that identifies this record's type
   * @param node The {@link JsonNode} holding the record's data
   * @param registrar The {@link ESMJsonParser.ParserRegistrar} to use for {@link Record}
   *     registration
   * @return The constructed {@link Record}
   * @throws InvocationTargetException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws NoSuchMethodException
   */
  public static Record createRecord(
      @NotNull String formId,
      @NotNull String editorId,
      @NotNull String signature,
      @NotNull JsonNode node,
      @NotNull ESMJsonParser.ParserRegistrar registrar)
      throws InvocationTargetException,
          InstantiationException,
          IllegalAccessException,
          NoSuchMethodException {
    // Determine the class to use for this new Record - defaults to Record if no specific class
    // found
    Class<?> clazz = SIGNATURE_TO_CLASS.computeIfAbsent(signature, s -> Record.class);
    Constructor<?> constructor =
        clazz.getConstructor(
            String.class,
            String.class,
            String.class,
            JsonNode.class,
            ESMJsonParser.ParserRegistrar.class);
    return (Record) constructor.newInstance(formId, editorId, signature, node, registrar);
  }

  /**
   * Factory method to create a {@link Record} from the raw ESM {@link JsonNode}
   *
   * @param node the raw {@link JsonNode} to map into a {@link Record}
   * @param registrar The {@link ESMJsonParser.ParserRegistrar} to use for {@link Record}
   *     registration
   * @return The derived {@link Record} object
   */
  public static @NotNull Record fromESMJsonObject(
      @NotNull JsonNode node, @NotNull ESMJsonParser.ParserRegistrar registrar) {
    // All Record objects must have a "Record Header" property
    Assert.assertTrue(node.isObject(), "Record objets must be of type 'Object'");
    JsonNode recordHdr =
        Assert.assertNotNull(node.get(Record.FLD_RECORD_HDR), "Missing " + Record.FLD_RECORD_HDR);
    Assert.assertTrue(recordHdr.isObject(), "Record Headers must be Json Objects");
    JsonNode formId =
        Assert.assertNotNull(recordHdr.get(Record.FLD_FORM_ID), "Missing " + Record.FLD_FORM_ID);
    Assert.assertTrue(formId.isTextual(), "Form IDs must be Json Strings");
    JsonNode signature =
        Assert.assertNotNull(
            recordHdr.get(Record.FLD_SIGNATURE), "Missing " + Record.FLD_SIGNATURE);
    Assert.assertTrue(signature.isTextual(), "Signatures must be Json Strings");
    JsonNode editorId = node.get(Record.FLD_EDITOR_ID);
    String editId = (null != editorId) ? editorId.asText() : "<no-edit-id>";

    try {
      return createRecord(formId.asText(), editId, signature.asText(), node, registrar);
    } catch (InvocationTargetException
        | InstantiationException
        | IllegalAccessException
        | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
}
