package net.dfr.providers.specification.operator;

import org.springframework.data.jpa.domain.Specification;

import net.dfr.core.converter.FilterValueConverter;
import net.dfr.core.filter.FilterParameter;
import net.dfr.core.operator.type.NotIn;

class SpecNotIn<T> implements NotIn<Specification<T>> {

	@SuppressWarnings("rawtypes")
	private static final SpecIn IN_STATIC = new SpecIn<>();

	@Override
	@SuppressWarnings("unchecked")
	public Specification<T> createFilter(FilterParameter filterParameter, FilterValueConverter filterValueConverter) {
		return Specification.not(IN_STATIC.createFilter(filterParameter, filterValueConverter));
	}

}
