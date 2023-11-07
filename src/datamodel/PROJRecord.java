package datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parser.ESMJsonParser;
import parser.Util;

/**
 * Abstraction of the PROJ record type
 *
 * @author Eric Karlson
 */
@ESMGroup(value = "PROJ")
public class PROJRecord extends Record {
  private static final String FLD_PROD = "PROD - Data";
  private static final String FLD_FLAGS = "Flags";
  private static final String FLD_SPEED = "Speed";
  private static final String FLD_EXPLOSION = "Explosion";

  public PROJRecord(
      @NotNull String formId,
      @NotNull String editorId,
      @NotNull String signature,
      @NotNull JsonNode node,
      ESMJsonParser.@NotNull ParserRegistrar registrar) {
    super(formId, editorId, signature, node, registrar);
  }

  public int getSpeed() {
    JsonNode prod = node.get(FLD_PROD);
    if (null == prod) {
      return 0;
    }
    JsonNode speed = prod.get(FLD_SPEED);
    return Util.asInt(speed, 0);
  }

  public @Nullable EXPLRecord getEXPLRecord() {
    JsonNode prod = node.get(FLD_PROD);
    if (null == prod) {
      return null;
    }

    // First check the "Flags"."Explosion" property to see whether we should apply
    // explosion effects for this projective
    JsonNode flags = prod.get(FLD_FLAGS);
    if (null == flags) {
      // No flags at all means no 'Explosion' flag which means no explosion effect
      return null;
    }
    if (0 == Util.asInt(flags.get(FLD_EXPLOSION), 0)) {
      // No explosion flag, or a flag of '0' means no explosion effect
      return null;
    }

    // We need to apply the explosion effect
    JsonNode expl = prod.get(FLD_EXPLOSION);
    return (null != expl && expl.isTextual())
        ? parser.findRecordByFormId(expl.asText(), EXPLRecord.class)
        : null;
  }
}
