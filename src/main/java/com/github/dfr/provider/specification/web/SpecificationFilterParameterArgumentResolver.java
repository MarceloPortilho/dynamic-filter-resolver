package com.github.dfr.provider.specification.web;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.github.dfr.annotation.And;
import com.github.dfr.annotation.Conjunction;
import com.github.dfr.annotation.Disjunction;
import com.github.dfr.annotation.Filter;
import com.github.dfr.annotation.Or;
import com.github.dfr.filter.FilterLogicalContext;
import com.github.dfr.filter.FilterParameterResolver;
import com.github.dfr.filter.LogicType;
import com.github.dfr.provider.AnnotationBasedFilterResolverProvider;

public class SpecificationFilterParameterArgumentResolver implements HandlerMethodArgumentResolver {

	private AnnotationBasedFilterResolverProvider filterResolverProvider;
	private FilterParameterResolver<Specification<?>> parameterFilter;

	public SpecificationFilterParameterArgumentResolver(StringValueResolver stringValueResolver,
			FilterParameterResolver<Specification<?>> parameterFilter) {
		this.filterResolverProvider = new AnnotationBasedFilterResolverProvider(stringValueResolver);
		this.parameterFilter = parameterFilter;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> parameterType = parameter.getParameterType();
		int annotationQuantity = countPresentAnnotations(parameter);
		if (annotationQuantity > 1) {
			throw new IllegalStateException(
					"The annotations Or, And, Conjunction and Disjunction cannot be used at the same time, as seen on parameter + "
							+ parameter.getParameterName());
		}
		return Specification.class.isAssignableFrom(parameterType) && annotationQuantity == 1;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		FilterLogicalContext logicalContext = null;

		And andAnnot;
		if ((andAnnot = parameter.getParameterAnnotation(And.class)) != null) {
			logicalContext = filterResolverProvider.createLogicContext(LogicType.CONJUNCTION, andAnnot.values(), null, webRequest.getParameterMap());
		}

		Or orAnnot;
		if (logicalContext == null && (orAnnot = parameter.getParameterAnnotation(Or.class)) != null) {
			logicalContext = filterResolverProvider.createLogicContext(LogicType.DISJUNCTION, orAnnot.values(), null, webRequest.getParameterMap());
		}

		Conjunction conjAnnot;
		if (logicalContext == null && (conjAnnot = parameter.getParameterAnnotation(Conjunction.class)) != null) {
			List<Filter[]> filtersList = new ArrayList<>(conjAnnot.values().length);
			for (Or or : conjAnnot.values()) {
				filtersList.add(or.values());
			}
			logicalContext = filterResolverProvider.createLogicContext(LogicType.CONJUNCTION, conjAnnot.and(), filtersList,
					webRequest.getParameterMap());
		}

		Disjunction disjAnnot;
		if (logicalContext == null && (disjAnnot = parameter.getParameterAnnotation(Disjunction.class)) != null) {
			List<Filter[]> filtersList = new ArrayList<>(disjAnnot.values().length);
			for (And and : disjAnnot.values()) {
				filtersList.add(and.values());
			}
			logicalContext = filterResolverProvider.createLogicContext(LogicType.DISJUNCTION, disjAnnot.or(), filtersList,
					webRequest.getParameterMap());
		}

		return parameterFilter.convertTo(logicalContext);
	}

	/**
	 * 
	 * @param parameter
	 */
	private int countPresentAnnotations(MethodParameter parameter) {
		int conjunctionFound = 0;
		int disjunctionFound = 0;
		int andFound = 0;
		int orFound = 0;

		for (Annotation annotation : parameter.getParameterAnnotations()) {
			if (annotation.annotationType().equals(Conjunction.class)) {
				conjunctionFound++;
			} else if (annotation.annotationType().equals(Disjunction.class)) {
				disjunctionFound++;
			} else if (annotation.annotationType().equals(And.class)) {
				andFound++;
			} else if (annotation.annotationType().equals(Or.class)) {
				orFound++;
			}
		}
		return conjunctionFound + disjunctionFound + andFound + orFound;
	}

}
