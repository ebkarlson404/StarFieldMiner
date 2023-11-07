package parser;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Overrides the standard {@link JsonNodeFactory} with our custom node factory that uses {@link
 * ESMObjectNode} instead of the standard {@link ObjectNode}. this allows the JSON parser to handle
 * the duplicate property keys emitted by the "serialize-command-json" xEdit script.
 *
 * @author Eric Karlson
 */
public class ESMNodeFactory extends JsonNodeFactory {
  /**
   * Return our customized {@link ESMObjectNode} to support the duplicate property names found in
   * the xEdit JSON export
   *
   * @return The {@link ObjectNode} to use inside the JSON parser
   */
  @Override
  public ObjectNode objectNode() {
    // Substitute our specialized ESMObjectNode for the standard ObjectNode
    return new ESMObjectNode(this);
  }
}
