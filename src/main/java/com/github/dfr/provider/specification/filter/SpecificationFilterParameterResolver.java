package com.github.dfr.provider.specification.filter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;

import com.github.dfr.decoder.FilterDecoder;
import com.github.dfr.decoder.FilterDecoderService;
import com.github.dfr.decoder.ParameterValueConverter;
import com.github.dfr.filter.CorrelatedFilterParameter;
import com.github.dfr.filter.FilterLogicContext;
import com.github.dfr.filter.FilterParameter;
import com.github.dfr.filter.FilterParameterResolver;

public class SpecificationFilterParameterResolver<T> implements FilterParameterResolver<Specification<T>> {

	private final FilterDecoderService<Specification<T>> filterDecoderService;
	private final ParameterValueConverter parameterValueConverter;

	public SpecificationFilterParameterResolver(FilterDecoderService<Specification<T>> decoderService,
			ParameterValueConverter parameterValueConverter) {
		this.filterDecoderService = decoderService;
		this.parameterValueConverter = parameterValueConverter;
	}

	@Override
	public Specification<T> convertTo(FilterLogicContext filterLogicContext) {
		if (filterLogicContext == null) {
			return Specification.where(null);
		}
		Map<String, Object> sharedContext = new HashMap<>();
		Specification<T> rootSpecification = createPredicates(filterLogicContext.getCorrelatedFilterParameter(), sharedContext);

		for (CorrelatedFilterParameter correlatedFilterParameter : filterLogicContext.getOppositeCorrelatedFilterParameters()) {
			Specification<T> specification = createPredicates(correlatedFilterParameter, sharedContext);
			if (specification != null) {
				if (rootSpecification == null) {
					rootSpecification = Specification.where(specification);
				} else {
					rootSpecification = filterLogicContext.isConjunction() ? rootSpecification.and(specification)
							: rootSpecification.or(specification);
				}
			}
		}
		return rootSpecification == null ? Specification.where(null) : rootSpecification;
	}

	/**
	 * 
	 * @param correlatedParameter
	 * @return
	 */
	private Specification<T> createPredicates(CorrelatedFilterParameter correlatedParameter, Map<String, Object> sharedContext) {
		if (correlatedParameter == null || correlatedParameter.getFilterParameters().isEmpty()) {
			return null;
		}
		Specification<T> rootSpec = null;
		for (FilterParameter filterParameter : correlatedParameter.getFilterParameters()) {
			FilterDecoder<Specification<T>> decoder = filterDecoderService.getFilterDecoderFor(filterParameter.getDecoder());
			Specification<T> spec = decoder.decode(filterParameter, parameterValueConverter, sharedContext);
			if (spec != null) {
				spec = filterParameter.isNegate() ? Specification.not(spec) : spec;
				if (rootSpec == null) {
					rootSpec = Specification.where(spec);
				} else {
					rootSpec = correlatedParameter.isConjunction() ? rootSpec.and(spec) : rootSpec.or(spec);
				}
			}
		}
		return rootSpec;
	}

}
