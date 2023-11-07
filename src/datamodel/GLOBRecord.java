package datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import parser.ESMJsonParser;

/**
 * Abstraction for the GLOB Records
 *
 * @author Eric Karlson
 */
@ESMGroup("GLOB")
public class GLOBRecord extends Record {
  public static final String UC02_UC_SHIP_COMPONENTS_UNLOCKED = "0010DA30";
  public static final String SHIP_BUILDER_ALLOW_LARGE_MODS = "00155F4A";
  public static final String SHIP_BUILDER_TEST_MODS = "00141C71";

  public GLOBRecord(
      @NotNull String formId,
      @NotNull String editorId,
      @NotNull String signature,
      @NotNull JsonNode node,
      ESMJsonParser.@NotNull ParserRegistrar registrar) {
    super(formId, editorId, signature, node, registrar);
  }
}
