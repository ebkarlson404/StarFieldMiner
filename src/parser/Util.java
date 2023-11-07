package parser;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Assert;

/**
 * Utility class that defines methods useful for operating on Form ID's, JsonNode's, casting etc.
 *
 * @author Eric Karlson
 */
public class Util {
  private static final Pattern DECORATED_FORMID_REGEX =
      Pattern.compile(".*\\[[^:]*:([0-9A-F]*)\\].*");

  /**
   * Extracts a raw Form ID from a <i>Decorated Form ID</i>. <i>Decorated Form IDs</i> have the
   * following syntax: {@code <EditorID>[<Signature>:<RawFormID>]}
   *
   * @param decoratedFormId The <i>Decorated Form ID</i>
   * @return The extracted <i>Raw Form ID</i>
   */
  public static @NotNull String toRaw(@NotNull String decoratedFormId) {
    Matcher matcher = DECORATED_FORMID_REGEX.matcher(decoratedFormId);
    Assert.assertTrue(matcher.find(), "Unable to extract raw form id from " + decoratedFormId);
    return matcher.group(1);
  }

  public static Integer asInt(@NotNull String value) {
    Assert.assertNotNull(value, "asInt: value is required to be non-null");
    return Double.valueOf(value).intValue();
  }

  public static Integer asInt(@Nullable String value, Integer dflt) {
    return (null != value) ? Double.valueOf(value).intValue() : dflt;
  }

  public static Integer asInt(@NotNull JsonNode value) {
    Assert.assertTrue(
        value.isNumber() || value.isTextual(), "asInt: value must be a number or a string");
    return asDouble(value).intValue();
  }

  public static Integer asInt(@Nullable JsonNode value, Integer dflt) {
    if (null == value) {
      return dflt;
    }
    Assert.assertTrue(
        value.isNumber() || value.isTextual(), "asInt: value must be a number or a string");
    return asDouble(value).intValue();
  }

  public static Double asDouble(@NotNull JsonNode value) {
    Assert.assertTrue(
        value.isNumber() || value.isTextual(), "asDouble: value must be a number or a string");
    return value.asDouble();
  }

  public static Double asDouble(@Nullable JsonNode value, Double dflt) {
    if (null == value) {
      return dflt;
    }
    Assert.assertTrue(
        value.isNumber() || value.isTextual(), "asDouble: value must be a number or a string");
    return value.asDouble();
  }

  public static <T> @Nullable T cast(@Nullable Object o, @NotNull Class<T> clazz) {
    return clazz.isInstance(o) ? clazz.cast(o) : null;
  }
}
