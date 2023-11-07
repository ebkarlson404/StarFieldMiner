package parser;

import com.fasterxml.jackson.databind.JsonNode;
import datamodel.*;
import org.jetbrains.annotations.NotNull;
import util.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class to create instances of {@link Record} based on the contents of the JSON <i>Record Header</i>
 *
 * @author Eric Karlson
 */
public class RecordFactory {
    private static final Map<String, Class<?>> SIGNATURE_TO_CLASS = new HashMap<>();

    static {
        SIGNATURE_TO_CLASS.put("AMMO", AMMORecord.class);
        SIGNATURE_TO_CLASS.put("AVIF", AVIFRecord.class);
        SIGNATURE_TO_CLASS.put("COBJ", COBJRecord.class);
        SIGNATURE_TO_CLASS.put("DMGT", DMGTRecord.class);
        SIGNATURE_TO_CLASS.put("EXPL", EXPLRecord.class);
        SIGNATURE_TO_CLASS.put("FLST", FLSTRecord.class);
        SIGNATURE_TO_CLASS.put("GBFM", GBFMRecord.class);
        SIGNATURE_TO_CLASS.put("KYWD", KYWDRecord.class);
        SIGNATURE_TO_CLASS.put("PERK", PERKRecord.class);
        SIGNATURE_TO_CLASS.put("PROJ", PROJRecord.class);
        SIGNATURE_TO_CLASS.put("WEAP", WEAPRecord.class);
    }

    public static Record createRecord(@NotNull String formId,
                                      @NotNull String editorId,
                                      @NotNull String signature,
                                      @NotNull JsonNode node,
                                      @NotNull ESMJsonParser parser)
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        // Determine the class to use for this new Record - defaults to Record if no specific class found
        Class<?> clazz = SIGNATURE_TO_CLASS.computeIfAbsent(signature, s -> Record.class);
        Constructor<?> constructor =
                clazz.getConstructor(String.class, String.class, String.class, JsonNode.class, ESMJsonParser.class);
        return (Record) constructor.newInstance(formId, editorId, signature, node, parser);
    }

    /**
     * Factory method to create a {@link Record} from the raw ESM {@link JsonNode}
     *
     * @param node   the raw {@link JsonNode} to map into a {@link Record}
     * @param parser The {@link ESMJsonParser} that is manaing this {@link Record}
     * @return The derived {@link Record} object
     */
    public static @NotNull Record fromESMJsonObject(@NotNull JsonNode node, @NotNull ESMJsonParser parser) {
        // All Record objects must have a "Record Header" property
        Assert.assertTrue(node.isObject(), "Record objets must be of type 'Object'");
        JsonNode recordHdr = Assert.assertNotNull(node.get(Record.FLD_RECORD_HDR), "Missing " + Record.FLD_RECORD_HDR);
        Assert.assertTrue(recordHdr.isObject(), "Record Headers must be Json Objects");
        JsonNode formId = Assert.assertNotNull(recordHdr.get(Record.FLD_FORM_ID), "Missing " + Record.FLD_FORM_ID);
        Assert.assertTrue(formId.isTextual(), "Form IDs must be Json Strings");
        JsonNode signature = Assert.assertNotNull(recordHdr.get(Record.FLD_SIGNATURE), "Missing " + Record.FLD_SIGNATURE);
        Assert.assertTrue(signature.isTextual(), "Signatures must be Json Strings");
        JsonNode editorId = node.get(Record.FLD_EDITOR_ID);
        String editId = (null != editorId) ? editorId.asText() : "<no-edit-id>";

        try {
            return createRecord(formId.asText(), editId, signature.asText(), node, parser);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
