package io.github.mportilho.dfr.core.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import io.github.mportilho.dfr.core.operator.FilterOperator;

@Documented
@Retention(RUNTIME)
public @interface Filter {

	/**
	 * <b>Optional:</b> Used to indicate the path to the attribute of the type's
	 * parameter type.
	 * 
	 * <p>
	 * For example: In a controller, the developer can reference to the DTO class
	 * and it's properties with <code>attributePath</code> and indicate the target
	 * JPA entity's attribute with <code>path</code>.
	 */
	String attributePath() default "";

	/**
	 * Name or path to the required attribute
	 * 
	 * <p>
	 * <b>Path</b> is the notation which the target attribute can be found from a
	 * specified root attribute, like <code>Person.addresses.streetName</code>
	 * 
	 */
	String path();

	/**
	 * Parameters needed to be supplied by the caller, exposed as input data
	 * requirements
	 */
	String[] parameters();

	/**
	 * Target attribute type for convertion
	 */
	Class<?> targetType() default Object.class;

	/**
	 * Operation to be used as a query filter
	 */
	@SuppressWarnings("rawtypes")
	Class<? extends FilterOperator> operator();

	/**
	 * Indicates that the logic of this filter must be negated.
	 * 
	 * <table>
	 * <tr>
	 * <th>Normal Logic</th>
	 * <th>Negated Logic</th>
	 * </tr>
	 * <tr>
	 * <th>A & B</th>
	 * <th>!(A & B)</th>
	 * </tr>
	 * </table>
	 * 
	 * <p>
	 * Can be parsed by the Spring Expression Language
	 */
	String negate() default "false";

	/**
	 * Default values for each parameter, each at the same position as
	 * <code>parameters</code>
	 * 
	 * <p>
	 * Can be parsed by the Spring Expression Language
	 */
	String[] defaultValues() default {};

	/**
	 * Constant values for each parameter. If this field has any value, the
	 * corresponding filter must not be requested from the user, as it will be used
	 * only internally
	 * 
	 * <p>
	 * Can be parsed by the Spring Expression Language
	 */
	String[] constantValues() default {};

	/**
	 * Optional format pattern to assist data conversion for the corresponding path
	 * type.
	 * 
	 * <p>
	 * Can be parsed by the Spring Expression Language
	 */
	String format() default "";

}
