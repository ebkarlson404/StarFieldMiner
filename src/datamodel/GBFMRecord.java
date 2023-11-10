package datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parser.ESMJsonParser;
import parser.Util;
import util.Assert;
import util.ESMKeyValueMap;

/**
 * Abstraction of all GBFM records
 *
 * @author Eric Karlson
 */
@ESMGroup(value = "GBFM")
public class GBFMRecord extends Record {
  private static final String FLD_COMPONENTS = "Components";
  private static final String FLD_COMPONENT = "Component";
  private static final String FLD_COMPONENT_DATA_FULL_NAME = "Component Data - Fullname";
  private static final String COMP_TYPE_PROPERTY_SHEET = "BGSPropertySheet_Component";
  private static final String COMP_TYPE_LINKED_FORMS = "BGSFormLinkData_Component";
  private static final String COMP_TYPE_KEYWORDS = "BGSKeywordForm_Component";
  private static final String COMP_TYPE_TES_FULL_NAME = "TESFullName_Component";

  /**
   * Encapsulates a <i>PropertySheet</i> found in a BGSPropertySheet_Component component. A
   * <i>PropertySheet</i> is essentially a map of AVIF keys to String values.
   */
  public static class PropertySheet extends JsonNodeWrapper {
    private static final String FLD_PROPERTIES = "PRPS - Properties";
    private static final String FLD_PROPERTY = "Property";
    private static final String FLD_ACTOR_VALUE = "Actor Value";
    private static final String FLD_VALUE = "Value";

    private final Map<String, String> propertyMap = new HashMap<>();

    private PropertySheet(JsonNode node, ESMJsonParser parser) {
      super(node);
      this.parser = parser;

      // Extract all the properties and store them in a map for easy access
      // This is another one of those cases where the 'serialize-command-json'
      // script generates JSON object with repeated property name, so use the
      // name-uniquifier logic from ESMKeyValueMap to enumerate all the repeated
      // properties.
      JsonNode properties = node.get(FLD_PROPERTIES);
      JsonNode property;
      for (int idx = 1;
          null
              != (property = properties.get(ESMKeyValueMap.generateRepeatedKey(FLD_PROPERTY, idx)));
          idx++) {
        propertyMap.put(getPropertyKey(property), getPropertyValue(property));
      }
    }

    private static @Nullable String getPropertyKey(JsonNode node) {
      JsonNode key = node.get(FLD_ACTOR_VALUE);
      return (null != key && key.isTextual()) ? key.asText() : null;
    }

    private static @Nullable String getPropertyValue(JsonNode node) {
      JsonNode key = node.get(FLD_VALUE);
      return (null != key && key.isTextual()) ? key.asText() : null;
    }

    public @Nullable String getPropertyValue(@NotNull String propertyFormId) {
      return propertyMap.get(propertyFormId);
    }

    public int getPropertyValueAsInt(@NotNull String propertyFormId, String errmsg) {
      return Double.valueOf(Assert.assertNotNull(getPropertyValue(propertyFormId), errmsg))
          .intValue();
    }

    public double getPropertyValueAsDouble(@NotNull String propertyFormId, String errmsg) {
      return Double.parseDouble(Assert.assertNotNull(getPropertyValue(propertyFormId), errmsg));
    }

    public double getPropertyValueAsDouble(
        @NotNull String propertyFormId, String errmsg, double dflt) {
      String res = getPropertyValue(propertyFormId);
      if (null == res) {
        System.err.println("WARN: " + errmsg + " [using default of " + dflt + "]");
        return dflt;
      }
      return Double.parseDouble(res);
    }
  }

  /** Encapsulates a <i>Full Name Data</i> found in a TESFullName_Component component. */
  public static class FullNameDataBlock extends JsonNodeWrapper {
    private FullNameDataBlock(JsonNode node) {
      super(node);
    }

    public String getFullName() {
      return getPropertyAsString(FLD_FULL_NAME);
    }
  }

  /** Encapsulates a <i>Component</i> found in the GBFM <i>Components</i> list */
  public static class Component extends JsonNodeWrapper {
    private static final String FLD_COMPONENT_TYPE = "BFCB - Component Type";
    private static final String FLD_COMPONENT_DATA_PROPERTY_SHEET =
        "Component Data - Property Sheet";
    private static final String FLD_COMPONENT_DATA_FORM_LINKS = "Component Data - Form Links";
    private static final String FLD_COMPONENT_DATA_KEYWORDS = "Component Data - Keywords";
    private static final String FLD_LINKED_FORMS = "Linked Forms";
    private static final String FLD_LINKED_FORM = "Linked Form";
    private static final String FLD_FORM_LINK_KEY = "FLKW - Keyword";
    private static final String FLD_FORM_LINK_FORM_ID = "FLFM - Linked Form";
    private static final String FLD_KEYWORDS = "Keywords";
    private static final String FLD_KEYWORD = "Keyword";
    private static final String FLD_KWDA = "KWDA - Keywords";

    private final ESMJsonParser parser;

    private Component(JsonNode node, ESMJsonParser parser) {
      super(node);
      this.parser = parser;
    }

    /**
     * @return The code that identifies the content of this {@link Component}
     */
    public @NotNull String getType() {
      return Assert.assertNotNull(
          getPropertyAsString(FLD_COMPONENT_TYPE), "Component is missing type property");
    }

    /**
     * Retrieves the {@link PropertySheet} from this {@link Component}, if there is one. Only
     * components of type <i>BGSPropertySheet_Component</i> have a {@link PropertySheet}.
     *
     * @return This Component's {@link PropertySheet} or {@code null} if there is none
     */
    public @Nullable PropertySheet getPropertySheet() {
      JsonNode propertySheet = node.get(FLD_COMPONENT_DATA_PROPERTY_SHEET);
      return (null != propertySheet) ? new PropertySheet(propertySheet, parser) : null;
    }

    /**
     * Find a linked Form ID in this {@link Component}, if there is one. Only components of type
     * <i>BGSFormLinkData_Component</i> have linked forms.
     *
     * @param keywordFormId The Form ID of the KEYW that identifies the linked form of interest
     * @return The Form ID of the linked form, or {@code null} if there is no such link
     */
    public @Nullable String getLinkedFormId(@NotNull String keywordFormId) {
      // Does this component have linked form data?
      JsonNode formLinks = node.get(FLD_COMPONENT_DATA_FORM_LINKS);
      if (null == formLinks) {
        return null;
      }

      // Go through all the linked forms to see if one has the indicated keyword
      JsonNode linkedForms = formLinks.get(FLD_LINKED_FORMS);
      Iterator<JsonNode> iter =
          (null != linkedForms) ? linkedForms.elements() : Collections.emptyIterator();
      while (iter.hasNext()) {
        JsonNode linkedFormRecord = iter.next().get(FLD_LINKED_FORM);
        if (null != linkedFormRecord) {
          JsonNode key = linkedFormRecord.get(FLD_FORM_LINK_KEY);
          if (null != key && key.isTextual() && Util.toRaw(key.asText()).equals(keywordFormId)) {
            JsonNode linkedForm = linkedFormRecord.get(FLD_FORM_LINK_FORM_ID);
            return (null != linkedForm && linkedForm.isTextual())
                ? Util.toRaw(linkedForm.asText())
                : null;
          }
        }
      }

      // No match found
      return null;
    }

    /**
     * Retrieve the first keyword tagged in this {@link Component} that satisfies some predicate.
     * Only components of type <i>BGSKeywordForm_Component</i> have tagged keywords.
     *
     * @param predicate The {@link Predicate} that the tagged KEYW must satisfy
     * @return The first tagged KEYW that satisfies the predicate, or {@code null}
     */
    public KYWDRecord getKeywordTag(Predicate<KYWDRecord> predicate) {
      JsonNode data = node.get(FLD_COMPONENT_DATA_KEYWORDS);
      if (null == data) {
        return null;
      }
      JsonNode keywords = data.get(FLD_KEYWORDS);
      if (null == keywords) {
        return null;
      }
      JsonNode keywdata = keywords.get(FLD_KWDA);
      if (null == keywdata) {
        return null;
      }

      // This is another case where the serialize-command-json generates a JSON Object
      // with repeated property names.  Use our key disambiguation logic to access each
      // renamed property that was created during parsing.
      JsonNode keywFormId;
      for (int idx = 1;
          null != (keywFormId = keywdata.get(ESMKeyValueMap.generateRepeatedKey(FLD_KEYWORD, idx)));
          idx++) {
        Assert.assertTrue(keywFormId.isTextual(), "Unexpected value type for 'Keyword' property");
        KYWDRecord kywd = parser.findRecordByFormId(keywFormId.asText(), KYWDRecord.class);
        if (null != kywd && predicate.test(kywd)) {
          return kywd;
        }
      }

      // No matching keyword found
      return null;
    }

    /**
     * Retrieves the {@link FullNameDataBlock} from this {@link Component}, if there is one. Only
     * components of type <i>TESFullName_Component</i> have a {@link PropertySheet}.
     *
     * @return This Component's {@link FullNameDataBlock} or {@code null} if there is none
     */
    public @Nullable FullNameDataBlock getFullnameDataBlock() {
      JsonNode data = node.get(FLD_COMPONENT_DATA_FULL_NAME);
      return (null != data) ? new FullNameDataBlock(data) : null;
    }
  }

  public GBFMRecord(
      @NotNull String formId,
      @NotNull String editorId,
      @NotNull String signature,
      @NotNull JsonNode node,
      ESMJsonParser.@NotNull ParserRegistrar registrar) {
    super(formId, editorId, signature, node, registrar);
  }

  /**
   * Finds the {@link Component} if the indicated type
   *
   * @param type The type code for the component of interest
   * @return The matching {@link Component} or {@code null} if not found
   */
  public @Nullable Component findComponent(@NotNull String type) {
    Iterator<Component> iter = getComponents();
    while (iter.hasNext()) {
      Component comp = iter.next();
      if (type.equals(comp.getType())) {
        return comp;
      }
    }
    return null;
  }

  /**
   * Retrieve the {@link PropertySheet} for this GBFM
   *
   * @return This GBFM's {@link PropertySheet} or {@code null} if there is none
   */
  public @Nullable PropertySheet getPropertySheet() {
    Component comp = findComponent(COMP_TYPE_PROPERTY_SHEET);
    return (null != comp) ? comp.getPropertySheet() : null;
  }

  /**
   * Retrieve the linked {@link WEAPRecord} for this GBFM
   *
   * @return The linked {@link WEAPRecord} or {@code null} if there is none
   */
  public WEAPRecord getWEAPRecord() {
    Component comp = findComponent(COMP_TYPE_LINKED_FORMS);
    if (null == comp) {
      return null;
    }
    String weapFormId = comp.getLinkedFormId(KYWDRecord.SPACESHIP_PART_LINKED_WEAPON_FID);
    return (null != weapFormId) ? parser.findRecordByFormId(weapFormId, WEAPRecord.class) : null;
  }

  /**
   * Retrieve the name of this object's manufacturer
   *
   * @return The object's manufacturer or {@code null} if there is none
   */
  public String getManufacturer() {
    Component comp = findComponent(COMP_TYPE_KEYWORDS);
    if (null == comp) {
      return null;
    }
    KYWDRecord kywd = comp.getKeywordTag(KYWDRecord::isShipModuleCorpNameKeyword);
    return (null != kywd) ? kywd.getFullName() : null;
  }

  /**
   * If this GBFM describes a Ship Module, find the modules class
   *
   * @return The Ship Module's class or {@code null} if there is none
   */
  public String getShipModuleClass() {
    Component comp = findComponent(COMP_TYPE_KEYWORDS);
    if (null == comp) {
      return null;
    }
    KYWDRecord kywd = comp.getKeywordTag(KYWDRecord::isShipModuleClassKeyword);
    return (null != kywd) ? kywd.getFullName() : null;
  }

  /**
   * Enumerates all {@link Component}'s found in this GBFM record
   *
   * @return An {@link Iterator} for the enumerated {@link Component}'s
   */
  public Iterator<Component> getComponents() {
    JsonNode components = node.get(FLD_COMPONENTS);
    final Iterator<JsonNode> iter =
        (null != components) ? components.elements() : Collections.emptyIterator();

    return new Iterator<>() {
      @Override
      public boolean hasNext() {
        return iter.hasNext();
      }

      @Override
      public Component next() {
        return new Component(iter.next().get(FLD_COMPONENT), parser);
      }
    };
  }

  /** {@inheritDoc} */
  @Override
  public String getFullName() {
    Component comp = findComponent(COMP_TYPE_TES_FULL_NAME);
    if (null == comp) {
      return null;
    }
    FullNameDataBlock data = comp.getFullnameDataBlock();
    if (null == data) {
      return null;
    }
    return data.getFullName();
  }
}
