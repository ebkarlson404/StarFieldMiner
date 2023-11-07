package miner;

import java.lang.annotation.*;

/**
 * Annotation used to mark all defined {@link IDataMiner} classes
 *
 * @author Eric Karlson
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataMiner {
  /** The name that identifies this data miner in the StarfieldMiner command line arguments */
  String value();
}
