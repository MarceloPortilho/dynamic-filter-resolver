package net.dfr.provider.specification.operator;

import static net.dfr.provider.specification.operator.PredicateUtils.computeAttributePath;

import javax.persistence.criteria.Path;

import org.springframework.data.jpa.domain.Specification;

import net.dfr.filter.FilterParameter;
import net.dfr.operator.FilterValueConverter;
import net.dfr.operator.type.LessOrEquals;

class SpecLessOrEquals<T> implements LessOrEquals<Specification<T>>, SpecComparablePredicate {

	@Override
	public Specification<T> createFilter(FilterParameter filterParameter, FilterValueConverter filterValueConverter) {
		return (root, query, criteriaBuilder) -> {
			Path<?> path = computeAttributePath(filterParameter, root);
			Object value = filterValueConverter.convert(filterParameter.findValue(), path.getJavaType(), filterParameter.findFormat());
			return toComparablePredicate(criteriaBuilder, path, value, criteriaBuilder::lessThanOrEqualTo, criteriaBuilder::le);
		};
	}

}
