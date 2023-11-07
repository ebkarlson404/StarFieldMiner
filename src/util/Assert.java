package util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for enforcing assertions
 *
 * @author Eric Karlson
 */
public class Assert {
    public static void assertTrue(boolean value, @NotNull String msg) {
        if (!value) {
            throw new AssertionError(msg);
        }
    }

    public static <T> @NotNull T assertNotNull(@Nullable T obj, @NotNull String msg) {
        assertTrue(null != obj, msg);
        return obj;
    }
}
