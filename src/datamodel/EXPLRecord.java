package datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import parser.ESMJsonParser;
import parser.Util;
import util.ESMKeyValueMap;

/**
 * Abstraction of the EXPL record type
 *
 * @author Eric Karlson
 */
@ESMGroup(value = "EXPL")
public class EXPLRecord extends Record {
  private static final String FLD_ENAM = "ENAM - Data";
  private static final String FLD_ATTACK_DAMAGE = "Unknown #2";
  private static final String FLD_DAMA = "DAMA - Damage Types";
  private static final String FLD_DAMAGE_TYPE = "Damage Type";
  private static final String FLD_VALUE = "Value";

  public EXPLRecord(
      @NotNull String formId,
      @NotNull String editorId,
      @NotNull String signature,
      @NotNull JsonNode node,
      ESMJsonParser.@NotNull ParserRegistrar registrar) {
    super(formId, editorId, signature, node, registrar);
  }

  public int getPhysicalDamage() {
    JsonNode enam = node.get(FLD_ENAM);
    if (null == enam) {
      return 0;
    }
    JsonNode dmg = enam.get(FLD_ATTACK_DAMAGE);
    return Util.asInt(dmg, 0);
  }

  public int getAuxDamage(@NotNull String damageTypeFormId) {
    JsonNode dama = node.get(FLD_DAMA);
    if (null == dama) {
      return 0;
    }

    // This is another example of where the serialize-command-json script generates JSON
    // Objects that have repeating property names.  Use our disambiguation logic to access
    // each repeated property with a unique name.
    JsonNode dmg;
    for (int idx = 1;
        null != (dmg = dama.get(ESMKeyValueMap.generateRepeatedKey(FLD_DAMAGE_TYPE, idx)));
        idx++) {
      JsonNode dtype = dmg.get(FLD_DAMAGE_TYPE);
      if (null != dtype && damageTypeFormId.equals(dtype.asText())) {
        JsonNode value = dmg.get(FLD_VALUE);
        return Util.asInt(value, 0);
      }
    }

    // No auxiliary damage info of this type -> 0
    return 0;
  }
}
