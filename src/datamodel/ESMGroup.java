package datamodel;

import parser.RecordFactory;

import java.lang.annotation.*;

/**
 * Annotation that associates a {@link Record} class with its associated "GRUP Top" tag.
 *
 * <p>Note that currently one must manually add any class annotated with this annotation
 * to the {@code static} block in {@link RecordFactory} as I do not have a way to scan
 * all classes to determine which ones are annotated with this annotation.
 *
 * @author Eric Karlson
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ESMGroup {
    String value();
}
