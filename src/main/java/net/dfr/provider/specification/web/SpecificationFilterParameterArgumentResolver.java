package net.dfr.provider.specification.web;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import net.dfr.annotation.Conjunction;
import net.dfr.annotation.Disjunction;
import net.dfr.filter.ConditionalStatement;
import net.dfr.filter.DynamicFilterResolver;
import net.dfr.provider.SpecificationConditionalStatementProvider;
import net.dfr.provider.specification.annotation.Fetches;

public class SpecificationFilterParameterArgumentResolver implements HandlerMethodArgumentResolver {

	private SpecificationConditionalStatementProvider<?> conditionalStatementProvider;
	private DynamicFilterResolver<?> dynamicFilterResolver;

	public SpecificationFilterParameterArgumentResolver(StringValueResolver stringValueResolver, DynamicFilterResolver<?> dynamicFilterResolver) {
		this.conditionalStatementProvider = new SpecificationConditionalStatementProvider<>(stringValueResolver);
		this.dynamicFilterResolver = dynamicFilterResolver;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> parameterType = parameter.getParameterType();
		if (!parameterType.isInterface() || !Specification.class.isAssignableFrom(parameterType)) {
			return false;
		}
		int countAnnotations = countAnnotationsUntilTwo(parameter);
		if (countAnnotations > 1) {
			throw new IllegalStateException(String.format(
					"Multiples annotations of Conjunction and/or Disjunction cannot be used at the same time, as seen on parameter %s of class '%s",
					parameter.getParameterName(), parameter.getDeclaringClass()));
		}
		return countAnnotations == 1;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		Map<Object, Object[]> providedParameterValuesMap = new HashMap<>();

		HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
		providedParameterValuesMap.putAll(webRequest.getParameterMap());
		providedParameterValuesMap.putAll((Map<String, String[]>) httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
		Fetches fetches = parameter.getParameterAnnotation(Fetches.class);
		if (fetches != null) {
			providedParameterValuesMap.put(Fetches.class, new Object[] { fetches });
		}

		ConditionalStatement statement = conditionalStatementProvider.createConditionalStatements(
				(Class<Specification<?>>) parameter.getParameterType(), parameter.getParameterAnnotations(), providedParameterValuesMap);
		return (statement != null && statement.hasAnyCondition()) ? dynamicFilterResolver.convertTo(statement) : Specification.where(null);
	}

	/**
	 * 
	 * @param parameter
	 */
	private int countAnnotationsUntilTwo(MethodParameter parameter) {
		int count = 0;
		for (Annotation annotation : parameter.getParameterAnnotations()) {
			if (annotation.annotationType().equals(Conjunction.class) || annotation.annotationType().equals(Disjunction.class)) {
				count++;
			}
			if (count > 1) {
				return count;
			}
		}
		return count;
	}

}
