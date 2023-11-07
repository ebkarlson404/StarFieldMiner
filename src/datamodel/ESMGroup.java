package datamodel;

import java.lang.annotation.*;
import parser.RecordFactory;

/**
 * Annotation that associates a {@link Record} class with its associated "GRUP Top" tag.
 *
 * <p>Note that currently one must manually add any class annotated with this annotation to the
 * {@code static} block in {@link RecordFactory} as I have not written any sort of annotation
 * processor for this annotation yet.
 *
 * @author Eric Karlson
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ESMGroup {
  /** The <i>ESM Record Signature</i> that is associated with this class */
  String value();
}
