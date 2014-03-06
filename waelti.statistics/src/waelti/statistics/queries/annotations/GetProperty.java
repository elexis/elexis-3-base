package waelti.statistics.queries.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as getter. Used by the view to determine which elements can be changed by the
 * user. This is a model driven design. The value of this annotation is used to describe the field
 * in the view.
 * 
 * @param Value
 *            is a string which will be displayed to describe the input which is expected.
 * @param Index
 *            is a integer which defines the order in which the fields are displayed.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GetProperty {
	
	/**
	 * Field description. There has to be a setter method annotated with a SetProperty method with
	 * the same value to have any effect on the query.
	 */
	public String value();
	
	/** Field index. Defines the order in which the fields are displayed. */
	public int index();
}
