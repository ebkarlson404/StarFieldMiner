package datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import parser.ESMJsonParser;

/**
 * Abstraction of the PERK records
 *
 * @author Eric Karlson
 */
@ESMGroup(value = "PERK")
public class PERKRecord extends BGSRecord {
  public static final String SKILL_STARSHIP_DESIGN_FID = "002C59DC";

  public PERKRecord(
      @NotNull String formId,
      @NotNull String editorId,
      @NotNull String signature,
      @NotNull JsonNode node,
      ESMJsonParser.@NotNull ParserRegistrar registrar) {
    super(formId, editorId, signature, node, registrar);
  }
}
