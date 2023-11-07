package datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import parser.ESMJsonParser;

/**
 * Abstraction of the DMGT records
 *
 * @author Eric Karlson
 */
public class DMGTRecord extends Record {
  public static final String SHIELD_FID = "0001EDE8";
  public static final String ELECTROMAGNETIC_FID = "00023190";

  public DMGTRecord(
      @NotNull String formId,
      @NotNull String editorId,
      @NotNull String signature,
      @NotNull JsonNode node,
      ESMJsonParser.@NotNull ParserRegistrar registrar) {
    super(formId, editorId, signature, node, registrar);
  }
}
