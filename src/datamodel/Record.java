package datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parser.ESMJsonParser;
import parser.JsonNodeWrapper;

/**
 * Encapsulates properties that are common across all ESM Record Objects
 *
 * @author Eric Karlson
 */
public class Record extends JsonNodeWrapper {
    static final String FLD_FULL_NAME = "FULL - Name";
    public static String FLD_RECORD_HDR = "Record Header";
    public static String FLD_SIGNATURE = "Signature";
    public static String FLD_FORM_ID = "FormID";
    public static String FLD_EDITOR_ID = "EDID - Editor ID";

    protected final String formId;
    protected final String editorId;
    protected final String signature;
    protected final ESMJsonParser parser;

    public Record(@NotNull String formId,
                  @NotNull String editorId,
                  @NotNull String signature,
                  @NotNull JsonNode node,
                  @NotNull ESMJsonParser parser) {
        super(node);
        this.formId = formId;
        this.editorId = editorId;
        this.signature = signature;
        this.parser = parser;

        // Register the new record with the parser
        parser.registerRecord(this);
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

    public @Nullable String getFullName() {
        return getPropertyAsString(FLD_FULL_NAME);
    }

    public @NotNull ESMJsonParser getParser() {
        return parser;
    }

}
