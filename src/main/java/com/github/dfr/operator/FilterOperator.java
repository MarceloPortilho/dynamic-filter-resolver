package com.github.dfr.operator;

import java.util.function.Function;

import com.github.dfr.filter.FilterParameter;

public interface FilterOperator<T> {

	T createFilter(FilterParameter filterParameter, FilterValueConverter filterValueConverter);

	default <P, R> R transformNonNull(P value, Function<P, R> function) {
		if (value != null) {
			return function.apply(value);
		}
		return null;
	}

}
