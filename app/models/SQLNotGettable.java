package models;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * When adding this annotation to a field, {@link dbconnect.DBConnect}'s generateRequiredColumn method will ignore this field
 * please add this to a field when the field cannot be got the DB query result
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLNotGettable {
}
