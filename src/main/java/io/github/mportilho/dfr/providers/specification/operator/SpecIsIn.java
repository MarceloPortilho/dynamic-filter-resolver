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

package io.github.mportilho.dfr.providers.specification.operator;

import static io.github.mportilho.dfr.providers.specification.operator.PredicateUtils.computeAttributePath;

import java.util.Collection;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import io.github.mportilho.dfr.core.converter.FilterValueConverter;
import io.github.mportilho.dfr.core.filter.FilterParameter;
import io.github.mportilho.dfr.core.operator.type.IsIn;

/**
 * Implementation of {@link IsIn} for the Spring Data JPA's
 * {@link Specification} interface
 * 
 * @author Marcelo Portilho
 *
 * @param <T>
 */
class SpecIsIn<T> implements IsIn<Specification<T>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Specification<T> createFilter(FilterParameter filterParameter, FilterValueConverter filterValueConverter) {
		return (root, query, criteriaBuilder) -> {
			Path<?> path = computeAttributePath(filterParameter, root);
			Object[] rawValues = extractArrayFromParameter(filterParameter.getValues());
			Predicate predicate = null;

			if (rawValues != null) {
				Object[] value = new Object[rawValues.length];
				for (int i = 0; i < value.length; i++) {
					value[i] = filterValueConverter.convert(rawValues[i], path.getJavaType(), filterParameter.getFormat());
				}
				predicate = path.in(value);
			}
			return predicate;
		};
	}

	/**
	 * Normalize array parameter for 'IN' operation
	 * 
	 * @param paramValues
	 * @return
	 */
	private Object[] extractArrayFromParameter(Object[] paramValues) {
		if (paramValues == null || paramValues.length == 0) {
			return null;
		} else if (!paramValues[0].getClass().isArray() && !Collection.class.isAssignableFrom(paramValues[0].getClass())) {
			throw new IllegalArgumentException("Expecting parameter value to be a collection of elements for 'in' operation");
		} else if (paramValues[0].getClass().isArray()) {
			return (Object[]) paramValues[0];
		}
		return ((Collection<?>) paramValues[0]).toArray();
	}

}