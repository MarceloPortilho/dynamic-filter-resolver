package net.dfr.providers.specification.operator;

import static net.dfr.providers.specification.operator.PredicateUtils.computeAttributePath;

import javax.persistence.criteria.Path;

import org.springframework.data.jpa.domain.Specification;

import net.dfr.core.converter.FilterValueConverter;
import net.dfr.core.filter.FilterParameter;
import net.dfr.core.operator.type.Less;

class SpecLess<T> implements Less<Specification<T>>, SpecComparablePredicate {

	@Override
	public Specification<T> createFilter(FilterParameter filterParameter, FilterValueConverter filterValueConverter) {
		return (root, query, criteriaBuilder) -> {
			Path<?> path = computeAttributePath(filterParameter, root);
			Object value = filterValueConverter.convert(filterParameter.findValue(), path.getJavaType(), filterParameter.findFormat());
			return toComparablePredicate(criteriaBuilder, path, value, criteriaBuilder::lessThan, criteriaBuilder::lt);
		};
	}

}
