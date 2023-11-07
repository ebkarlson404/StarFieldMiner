package datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import parser.ESMJsonParser;

/**
 * Abstraction of the AMMO record type
 *
 * @author Eric Karlson
 */
@ESMGroup(value = "AMMO")
public class AMMORecord extends Record {
  private static final String FLD_DNAM = "DNAM - DNAM";
  private static final String FLD_PROJECTILE = "Projectile";

  public AMMORecord(
      @NotNull String formId,
      @NotNull String editorId,
      @NotNull String signature,
      @NotNull JsonNode node,
      ESMJsonParser.@NotNull ParserRegistrar registrar) {
    super(formId, editorId, signature, node, registrar);
  }

  public PROJRecord getPROJRecord() {
    JsonNode dnam = node.get(FLD_DNAM);
    if (null == dnam) {
      return null;
    }
    JsonNode proj = dnam.get(FLD_PROJECTILE);
    return (null != proj && proj.isTextual())
        ? parser.findRecordByFormId(proj.asText(), PROJRecord.class)
        : null;
  }
}
