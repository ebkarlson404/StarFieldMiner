package datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import parser.ESMJsonParser;

/**
 * Encapsulates properties that are common across all ESM Record Objects
 *
 * @author Eric Karlson
 */
public class Record extends JsonNodeWrapper {
  public static String FLD_RECORD_HDR = "Record Header";
  public static String FLD_SIGNATURE = "Signature";
  public static String FLD_FORM_ID = "FormID";
  public static String FLD_EDITOR_ID = "EDID - Editor ID";

  protected final String formId;
  protected final String editorId;
  protected final String signature;
  protected final ESMJsonParser parser;

  public Record(
      @NotNull String formId,
      @NotNull String editorId,
      @NotNull String signature,
      @NotNull JsonNode node,
      @NotNull ESMJsonParser.ParserRegistrar registrar) {
    super(node);
    this.formId = formId;
    this.editorId = editorId;
    this.signature = signature;
    this.parser = registrar.getParser();

    // Now self-register
    registrar.register(this);
  }

  @Override
  public String toString() {
    return String.format("%s [%s:%s]", getEditorId(), getSignature(), getFormId());
  }

  public @NotNull String getFormId() {
    return formId;
  }

  public @NotNull String getEditorId() {
    return editorId;
  }

  public @NotNull String getSignature() {
    return signature;
  }

  public @NotNull ESMJsonParser getParser() {
    return parser;
  }
}
