package parser;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for any class that wraps a {@link JsonNode}.  Provides basic utilities for operating on the {@link JsonNode}
 *
 * @author Eric Karlson
 */
public class JsonNodeWrapper {
    /**
     * The wrapped {@link JsonNode}
     */
    protected final JsonNode node;

    protected JsonNodeWrapper(@NotNull JsonNode node) {
        this.node = node;
    }

    /**
     * @return The {@link JsonNode} that this class wraps
     */
    public @NotNull JsonNode getNode() {
        return node;
    }

    /**
     * Retrieve the value of a simple, scalar String property
     *
     * @param propName The name of the property to retrieve
     * @return The value of the property as a {@link String} if the property exists and is a scalar String value, {@code null} otherwise
     */
    protected @Nullable String getPropertyAsString(@NotNull String propName) {
        JsonNode value = node.get(propName);
        return (null != value && value.isTextual()) ? value.asText() : null;
    }
}
