package datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parser.ESMJsonParser;
import parser.JsonNodeWrapper;
import parser.Util;

/**
 * Abstraction of the COBJ records
 *
 * @author Eric Karlson
 */
@ESMGroup(value = "COBJ")
public class COBJRecord extends Record {
  private static final String FLD_CONDITIONS = "Conditions";
  private static final String FLD_CONDITION = "Condition";
  private static final String FLD_REQD_PERKS = "RQPK - Required Perks";
  private static final String FLD_REQUIRED_PERK_FMT = "Required Perk #%d";
  private static final String FLD_CREATED_OBJECT = "CNAM - Created Object";
  private static final String FLD_COST_DATA = "DATA - Value";
  private static final String FLD_CATEGORY = "FNAM - Category";
  private static final String FLD_KEYWORD = "Keyword";

  public static class Condition extends JsonNodeWrapper {
    private static final String FLD_CTDA = "CTDA - CTDA";
    private static final String FLD_COMPARISON_VALUE = "Comparison Value";
    private static final String FLD_FUNCTION = "Function";
    private static final String FLD_RUN_ON = "Run On";
    private static final String FLD_TYPE = "Type";
    private static final String FUNC_HAS_KEYWORD = "HasKeyword";
    private static final String FUNC_GET_LEVEL = "GetLevel";
    private static final String FUNC_GET_GLOBAL_VALUE = "GetGlobalValue";
    private static final String FLD_PARAM_1 = "Parameter #1";
    private static final String RUN_ON_SUBJECT = "Subject";
    private static final String RUN_ON_REFERENCE = "Reference";

    public enum ConditionType {
      EQUAL("10000000"),
      EQUAL_OR_GREATER("11000000");

      private static final Map<String, ConditionType> CODE_TO_ENUM = new HashMap<>();

      static {
        for (ConditionType type : values()) {
          CODE_TO_ENUM.put(type.getCode(), type);
        }
      }

      private final String code;

      ConditionType(String code) {
        this.code = code;
      }

      public String getCode() {
        return code;
      }

      public static @Nullable ConditionType fromCode(String code) {
        return CODE_TO_ENUM.get(code);
      }
    }

    private Condition(JsonNode node) {
      super(node);
    }

    @Override
    public String toString() {
      return "Condition:" + node;
    }

    private @Nullable String getCTDAField(@NotNull String field) {
      JsonNode ctda = node.get(FLD_CTDA);
      JsonNode value = (null != ctda) ? ctda.get(field) : null;
      return (null != value && value.isTextual()) ? value.asText() : null;
    }

    public @Nullable String getComparisonValue() {
      return getCTDAField(FLD_COMPARISON_VALUE);
    }

    public @Nullable String getFunction() {
      return getCTDAField(FLD_FUNCTION);
    }

    public @Nullable String getRunOn() {
      return getCTDAField(FLD_RUN_ON);
    }

    public @Nullable String getParam1() {
      return getCTDAField(FLD_PARAM_1);
    }

    public @Nullable ConditionType getConditionType() {
      return ConditionType.fromCode(getCTDAField(FLD_TYPE));
    }

    public boolean isVendorAvailabilityCondition() {
      return FUNC_HAS_KEYWORD.equals(getFunction())
          && RUN_ON_SUBJECT.equals(getRunOn())
          && ConditionType.EQUAL.equals(getConditionType())
          && 1 == Util.asInt(getComparisonValue(), 0);
    }

    public boolean isMinLevelCondition() {
      return FUNC_GET_LEVEL.equals(getFunction())
          && RUN_ON_REFERENCE.equals(getRunOn())
          && ConditionType.EQUAL_OR_GREATER.equals(getConditionType());
    }

    /**
     * Checks to see if this condition is a check on whether the Subject has a particular global
     * flag.
     *
     * @return The Form ID of the {@link GLOBRecord} or {@code null} if this is not a subject-global
     *     condition
     */
    public @Nullable String getSubjectGlobalCheckTarget() {
      return FUNC_GET_GLOBAL_VALUE.equals(getFunction())
              && RUN_ON_SUBJECT.equals(getRunOn())
              && ConditionType.EQUAL.equals(getConditionType())
              && 1 == Util.asInt(getComparisonValue(), 0)
          ? getParam1()
          : null;
    }
  }

  public static class RequiredPerk extends JsonNodeWrapper {
    private static final String FLD_PERK = "Perk";
    private static final String FLD_RANK = "Rank";

    private final ESMJsonParser parser;

    private RequiredPerk(JsonNode node, ESMJsonParser parser) {
      super(node);
      this.parser = parser;
    }

    @Override
    public String toString() {
      return "RequiredPerk:" + node;
    }

    public String getPerkFormId() {
      return getPropertyAsString(FLD_PERK);
    }

    public @Nullable PERKRecord getPerk() {
      String perkFormId = getPerkFormId();
      return (null != perkFormId) ? parser.findRecordByFormId(perkFormId, PERKRecord.class) : null;
    }

    public Integer getRank() {
      JsonNode rank = node.get(FLD_RANK);
      return Util.asInt(rank, null);
    }
  }

  public COBJRecord(
      @NotNull String formId,
      @NotNull String editorId,
      @NotNull String signature,
      @NotNull JsonNode node,
      ESMJsonParser.@NotNull ParserRegistrar registrar) {
    super(formId, editorId, signature, node, registrar);
  }

  /**
   * @return The constructible object's cost
   */
  public int getCost() {
    JsonNode cost = node.get(FLD_COST_DATA);
    return Util.asInt(cost, 0);
  }

  /**
   * Enumerates all the {@link Condition}'s in the COBJ record
   *
   * @return An {@link Iterator} for the enumerated {@link Condition}'s
   */
  public @NotNull Iterator<Condition> getConditions() {
    JsonNode conditions = node.get(FLD_CONDITIONS);
    if (null == conditions) {
      return Collections.emptyIterator();
    }
    final Iterator<JsonNode> iter = conditions.elements();

    return new Iterator<>() {
      @Override
      public boolean hasNext() {
        return iter.hasNext();
      }

      @Override
      public Condition next() {
        return new Condition(iter.next().get(FLD_CONDITION));
      }
    };
  }

  /**
   * Enumerate all the {@link RequiredPerk}'s in this COBJ record
   *
   * @return An {@link Iterator} for the enumerated {@link RequiredPerk}'s
   */
  public @NotNull Iterator<RequiredPerk> getRequiredPerks() {
    final JsonNode perks = node.get(FLD_REQD_PERKS);
    if (null == perks) {
      return Collections.emptyIterator();
    }
    final ESMJsonParser thisParser = this.parser;
    return new Iterator<>() {
      private int idx = 0;

      @Override
      public boolean hasNext() {
        return null != perks.get(String.format(FLD_REQUIRED_PERK_FMT, idx));
      }

      @Override
      public RequiredPerk next() {
        JsonNode perk = perks.get(String.format(FLD_REQUIRED_PERK_FMT, idx++));
        if (null == perk) {
          throw new NoSuchElementException();
        }
        return new RequiredPerk(perk, thisParser);
      }
    };
  }

  /**
   * Retrieve the type of objected created by this COBJ record.
   *
   * @param clazz The expected {@link Class} of the created object, or {@code Record.class} for any
   *     type of {@link Record}
   * @param <T> The expected class of the created object
   * @return The {@link Record} for the type of created object, or {@code null} if one cannot be
   *     found
   */
  public <T extends Record> @Nullable T getCreatedObject(@NotNull Class<T> clazz) {
    // Find the FormID for the Created Object
    JsonNode formId = node.get(FLD_CREATED_OBJECT);
    if (null == formId || !formId.isTextual()) {
      return null;
    }

    // Note that there are two choices here:
    // 1. A GBFM for the actual created object
    // 2. A FLST which indicates several possible created objects
    // Option 2 occurs for Turrets as there are 4 variations for each turret, corresponding
    // to the 4 orientations.  In principle, other than the turret facing, all four
    // should be identical (I'm assuming - I've not tried to verify this).
    // So if we find that we get an FLST record, we use the first GBFM from that FLST
    // Start by assuming that the reference points at an FLST record
    String rawFormId = Util.toRaw(formId.asText());
    FLSTRecord flst = parser.findRecordByFormId(rawFormId, FLSTRecord.class);
    if (null != flst) {
      // Grab the first GBFM from the FLST
      return flst.at(0, clazz);
    }

    // Otherwise assume that it must be a direct reference
    return parser.findRecordByFormId(rawFormId, clazz);
  }

  /**
   * @return The FormID for this COBJ's <i>category</i> keyword
   */
  public @Nullable String getCategoryFormId() {
    JsonNode cat = node.get(FLD_CATEGORY);
    if (null == cat) {
      return null;
    }
    JsonNode keyw = cat.get(FLD_KEYWORD);
    return (null != keyw && keyw.isTextual()) ? keyw.asText() : null;
  }
}
