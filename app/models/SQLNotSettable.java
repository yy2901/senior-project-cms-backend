package models;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * When adding this annotation to a field, {@link helpers.SqlGenerator}'s parsePojo method will ignore this field
 * please add this to a field when the field cannot be set in an SQL query
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLNotSettable {
}
