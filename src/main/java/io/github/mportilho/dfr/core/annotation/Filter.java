/*MIT License

Copyright (c) 2021 Marcelo Portilho

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.*/

package io.github.mportilho.dfr.core.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import io.github.mportilho.dfr.core.operator.FilterOperator;

/**
 * Defines a logic clause, indicating it's parameters and operation to be
 * applied
 * 
 * @author Marcelo Portilho
 *
 */
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
	 * 
	 * @return Optional path for another type's attribute
	 */
	String attributePath() default "";

	/**
	 * <b>Path</b> is the notation which the target attribute can be found from a
	 * specified root attribute, like <code>Person.addresses.streetName</code>
	 * 
	 * @return Name or path to the required attribute
	 * 
	 */
	String path();

	/**
	 * @return Parameters needed to be supplied by the caller, exposed as input data
	 *         requirements
	 */
	String[] parameters();

	/**
	 * @return Target attribute type for convertion
	 */
	Class<?> targetType() default Object.class;

	/**
	 * @return Operation to be used as a query filter
	 */
	@SuppressWarnings("rawtypes")
	Class<? extends FilterOperator> operator();

	/**
	 * Indicates that the logic of this filter must be negated. Can be parsed by the
	 * Spring Expression Language
	 * 
	 * <p>
	 * Normal Logic: <b>A &amp; B</b>
	 * 
	 * <p>
	 * Negated Logic: <b>!(A &amp; B)</b>
	 * 
	 * @return Indication if resulting clause must have it's result negated
	 */
	String negate() default "false";

	/**
	 * @return Indication to try to ignore case while processing the dynamic filter
	 *         if the comparison attribute type is a {@link String}
	 */
	boolean ignoreCase() default false;

	/**
	 * @return Default values for each parameter, each at the same position as
	 *         <code>parameters</code>. Can be parsed by the Spring Expression
	 *         Language
	 */
	String[] defaultValues() default {};

	/**
	 * @return Constant values for each parameter. If this field has any value, the
	 *         corresponding filter must not be requested from the user, as it will
	 *         be used only internally. Can be parsed by the Spring Expression
	 *         Language
	 */
	String[] constantValues() default {};

	/**
	 * @return Optional format pattern to assist data conversion for the
	 *         corresponding path type. Can be parsed by the Spring Expression
	 *         Language
	 */
	String format() default "";

}
