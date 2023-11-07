package datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import parser.ESMJsonParser;
import parser.Util;

/**
 * Abstraction for FLST records
 *
 * @author Eric Karlson
 */
@ESMGroup("FLST")
public class FLSTRecord extends Record {
    private static final String FLD_FORM_IDS = "FormIDs";
    private static final String FLD_LNAM ="LNAM - FormID";

    public FLSTRecord(@NotNull String formId, @NotNull String editorId, @NotNull String signature, @NotNull JsonNode node, @NotNull ESMJsonParser parser) {
        super(formId, editorId, signature, node, parser);
    }

    /**
     * Retrieve the Nth form in this list.
     * @param idx o-baed index of the form to retrieve
     * @param clazz The expected {@link Class} of the referenced form
     * @return A record of the indicated type from the indicated position in the list, or {@code} if there is none
     * @param <T> The expected class of the referenced form
     */
    public <T extends Record> T at(int idx, Class<T> clazz) {
        JsonNode forms = node.get(FLD_FORM_IDS);
        if (null == forms) {
            return null;
        }
        JsonNode form = forms.get(idx);
        if (null == form) {
            return null;
        }
        JsonNode lnam = form.get(FLD_LNAM);
        if (null == lnam || !lnam.isTextual()) {
            return null;
        }
        return parser.findRecordByFormId(Util.toRaw(lnam.asText()), clazz);
    }
}
