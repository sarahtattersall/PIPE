package pipe.models.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for serialising {@link PetriNetComponent} fields
 * Mark fields with this annotation to be written when saved out
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Pnml {
    /**
     *
     * @return name of field to be written to
     */
    String value();
}
