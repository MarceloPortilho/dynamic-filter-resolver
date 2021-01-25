package com.github.djs.provider.specification.decoder.type;

import static com.github.djs.provider.specification.decoder.type.PredicateUtils.computeAttributePath;

import java.util.Map;

import javax.persistence.criteria.Path;

import org.springframework.data.jpa.domain.Specification;

import com.github.djs.decoder.ValueConverter;
import com.github.djs.decoder.type.GreaterOrEquals;
import com.github.djs.filter.ParameterFilterMetadata;

class SpecGreaterOrEquals<T> implements GreaterOrEquals<Specification<T>>, SpecComparablePredicate {

	@Override
	public Specification<T> decode(ParameterFilterMetadata metadata, ValueConverter valueConverter, Map<String, Object> sharedContext) {
		return (root, query, criteriaBuilder) -> {
			Path<?> path = computeAttributePath(metadata, root);
			Object value = valueConverter.convert(metadata.findSingleValue(), path.getJavaType());
			return toComparablePredicate(criteriaBuilder, path, value, criteriaBuilder::greaterThanOrEqualTo, criteriaBuilder::ge);
		};
	}

}
