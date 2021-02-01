package com.github.dfr.provider;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.util.StringValueResolver;

import com.github.dfr.annotation.And;
import com.github.dfr.annotation.Conjunction;
import com.github.dfr.annotation.Disjunction;
import com.github.dfr.annotation.Filter;
import com.github.dfr.annotation.Or;
import com.github.dfr.filter.ConditionalStatement;
import com.github.dfr.filter.FilterParameter;
import com.github.dfr.filter.LogicType;

public class AnnotationBasedConditionalStatementProvider {

	private static final List<Class<?>> IGNORED_ANNOTATIONS = Arrays.asList(Documented.class, Retention.class, Target.class);

	private StringValueResolver stringValueResolver;

	public AnnotationBasedConditionalStatementProvider(StringValueResolver stringValueResolver) {
		super();
		this.stringValueResolver = stringValueResolver;
	}

	/**
	 * 
	 * @param <T>
	 * @param parameterInterface
	 * @param parameterAnnotations
	 * @param parametersMap
	 * @return
	 */
	public <T> ConditionalStatement createConditionalStatements(Class<?> parameterInterface, Annotation[] parameterAnnotations,
			Map<String, T[]> parametersMap) {
		List<ConditionalStatement> statements = new ArrayList<>();

		ConditionalStatement interfaceStatement = createConditionalStatementsFromParameterInterface(parameterInterface, parametersMap);
		if (interfaceStatement != null && interfaceStatement.hasAnyCondition()) {
			statements.add(interfaceStatement);
		}

		if (parameterAnnotations != null && parameterAnnotations.length != 0) {
			for (Annotation annotation : parameterAnnotations) {
				ConditionalStatement annotationStatement = createConditionalStatementsFromAnnotations(annotation, parametersMap);
				if (annotationStatement != null && annotationStatement.hasAnyCondition()) {
					statements.add(annotationStatement);
				}
			}
		}

		if (statements.isEmpty()) {
			return null;
		} else if (statements.size() == 1) {
			return statements.get(0);
		}
		return new ConditionalStatement(LogicType.CONJUNCTION, null, statements);
	}

	/**
	 * 
	 * @param <T>
	 * @param parameterAnnotations
	 * @param parametersMap
	 * @return
	 */
	private <T> ConditionalStatement createConditionalStatementsFromAnnotations(Annotation parameterAnnotation, Map<String, T[]> parametersMap) {
		if (parameterAnnotation == null) {
			return null;
		}
		List<ConditionalStatement> statements = new ArrayList<>();
		if (!parameterAnnotation.annotationType().equals(Conjunction.class) && !parameterAnnotation.annotationType().equals(Disjunction.class)) {
			for (Annotation annotation : parameterAnnotation.annotationType().getAnnotations()) {
				if (!IGNORED_ANNOTATIONS.contains(annotation.annotationType())) {
					ConditionalStatement stmt = createConditionalStatementsFromAnnotations(annotation, parametersMap);
					if (stmt != null && stmt.hasAnyCondition()) {
						statements.add(stmt);
					}
				}
			}
		}

		LogicType logicTypeTemp = null;
		Filter[] filters = null;
		List<Filter[]> inversedConditionFilters = null;
		if (Conjunction.class.equals(parameterAnnotation.annotationType())) {
			logicTypeTemp = LogicType.CONJUNCTION;
			Conjunction conjunction = (Conjunction) parameterAnnotation;
			filters = conjunction.value();
			inversedConditionFilters = Stream.of(conjunction.disjunctions()).filter(or -> or.value().length > 0).map(Or::value)
					.collect(Collectors.toList());
		} else if (Disjunction.class.equals(parameterAnnotation.annotationType())) {
			logicTypeTemp = LogicType.DISJUNCTION;
			Disjunction disjunction = (Disjunction) parameterAnnotation;
			filters = disjunction.value();
			inversedConditionFilters = Stream.of(disjunction.conjunctions()).filter(and -> and.value().length > 0).map(And::value)
					.collect(Collectors.toList());
		}
		if (logicTypeTemp != null) {
			LogicType logicType = logicTypeTemp;
			List<FilterParameter> clauses = createFilterParameters(filters, parametersMap);
			List<ConditionalStatement> invertedStatements = (inversedConditionFilters == null) ? Collections.emptyList() : //@formatter:off
				inversedConditionFilters
					.stream()
					.filter(Objects::nonNull)
					.map(filter -> createFilterParameters(filter, parametersMap))
					.filter(l -> l.size() > 0)
					.map(params -> new ConditionalStatement(logicType.opposite(), params))
					.collect(Collectors.toList())
			;//@formatter:on

			if (clauses.isEmpty() && !invertedStatements.isEmpty() && invertedStatements.size() == 1) {
				statements.add(invertedStatements.get(0));
			} else if (!clauses.isEmpty() || !invertedStatements.isEmpty()) {
				statements.add(new ConditionalStatement(logicType, clauses, invertedStatements));
			}
		}

		if (statements.isEmpty()) {
			return null;
		} else if (statements.size() == 1) {
			return statements.get(0);
		}
		return new ConditionalStatement(LogicType.CONJUNCTION, null, statements);
	}

	/**
	 * 
	 * @param <T>
	 * @param parameterInterface
	 * @param parametersMap
	 * @return
	 */
	private <T> ConditionalStatement createConditionalStatementsFromParameterInterface(Class<?> parameterInterface, Map<String, T[]> parametersMap) {
		List<ConditionalStatement> statements = new ArrayList<>();
		for (Class<?> subInterfaces : parameterInterface.getInterfaces()) {
			ConditionalStatement stmt = createConditionalStatementsFromParameterInterface(subInterfaces, parametersMap);
			if (stmt != null && stmt.hasAnyCondition()) {
				statements.add(stmt);
			}
		}

		Annotation[] parameterAnnotations = parameterInterface.getAnnotations();
		if (parameterAnnotations != null && parameterAnnotations.length != 0) {
			for (Annotation annotation : parameterAnnotations) {
				ConditionalStatement annotationStatement = createConditionalStatementsFromAnnotations(annotation, parametersMap);
				if (annotationStatement != null && annotationStatement.hasAnyCondition()) {
					statements.add(annotationStatement);
				}
			}
		}

		if (statements.isEmpty()) {
			return null;
		} else if (statements.size() == 1) {
			return statements.get(0);
		}
		return new ConditionalStatement(LogicType.CONJUNCTION, null, statements);
	}

	/**
	 * 
	 * @param <T>
	 * @param filters
	 * @param parametersMap
	 * @return
	 */
	private <T> List<FilterParameter> createFilterParameters(Filter[] filters, Map<String, T[]> parametersMap) {
		if (filters == null || filters.length == 0) {
			return Collections.emptyList();
		}

		List<FilterParameter> filterParameters = new ArrayList<>();
		for (Filter filter : filters) {
			if (hasAnyParameterProvidedOrConstants(parametersMap, filter)) {
				Object[] values = null;
				if (hasAnyConstantValue(filter)) {
					values = generateValuesFromConstants(filter);
				} else {
					values = generateValuesFromProvidedParameters(filter, parametersMap);
				}

				boolean negate = Boolean.parseBoolean(computeSpringExpressionLanguage(filter.negate()));
				String[] formats = computeSpringExpressionLanguage(filter.formats());

				filterParameters.add(
						new FilterParameter(filter.path(), filter.parameters(), filter.targetType(), filter.operator(), negate, values, formats));
			}
		}
		return filterParameters;
	}

	/**
	 * 
	 * @param <T>
	 * @param filter
	 * @param parametersMap
	 * @return
	 */
	private <T> Object[] generateValuesFromProvidedParameters(Filter filter, Map<String, T[]> parametersMap) {
		Object[] values = computeProvidedValues(filter, parametersMap);
		if (values.length > 0) {
			String[] defaultValues = filter.defaultValues();
			for (int i = 0; i < values.length; i++) {
				if (values[i] == null && defaultValues.length > i) {
					values[i] = computeSpringExpressionLanguage(defaultValues[i]);
				}
			}
		}
		return values;
	}

	/**
	 * 
	 * @param filter
	 * @return
	 */
	private Object[] generateValuesFromConstants(Filter filter) {
		if (filter.constantValues().length > filter.parameters().length) {
			throw new IllegalStateException(String.format("Found more constant values declared than required parameters '%s'",
					String.join(", ", Arrays.asList(filter.parameters()))));
		}
		return computeSpringExpressionLanguage(filter.constantValues());
	}

	/**
	 * 
	 * @param <T>
	 * @param parametersMap
	 * @param filter
	 * @return
	 */
	private <T> Object[] computeProvidedValues(Filter filter, Map<String, T[]> parametersMap) {
		Object[] values = new Object[filter.parameters().length];
		if (parametersMap != null && !parametersMap.isEmpty()) {
			for (int i = 0; i < values.length; i++) {
				Object[] paramValues = parametersMap.get(filter.parameters()[i]);
				if (paramValues != null && paramValues.length != 0) {
					values[i] = paramValues.length == 1 ? paramValues[0] : paramValues;
				} else {
					values[i] = paramValues;
				}
			}
		}
		return values;
	}

	/**
	 * 
	 * @param expressions
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> T[] computeSpringExpressionLanguage(String[] expressions) {
		if (stringValueResolver != null && expressions != null && expressions.length != 0) {
			Object[] computed = new String[expressions.length];
			for (int i = 0; i < expressions.length; i++) {
				computed[i] = computeSpringExpressionLanguage(expressions[i]);
			}
			return (T[]) computed;
		}
		return (T[]) expressions;
	}

	/**
	 * 
	 * @param <T>
	 * @param expression
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> T computeSpringExpressionLanguage(String expression) {
		if (stringValueResolver != null && expression != null && !expression.isEmpty()) {
			return (T) stringValueResolver.resolveStringValue(expression);
		}
		return (T) expression;
	}

	/**
	 * 
	 * @param <T>
	 * @param parametersMap
	 * @param filter
	 * @return
	 */
	private <T> boolean hasAnyParameterProvidedOrConstants(Map<String, T[]> parametersMap, Filter filter) {
		if (hasAnyConstantValue(filter) || hasAnyDefaultValue(filter)) {
			return true;
		}
		for (String filterParams : filter.parameters()) {
			if (parametersMap.keySet().contains(filterParams)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param filter
	 * @return
	 */
	private boolean hasAnyConstantValue(Filter filter) {
		if (filter.constantValues() != null && filter.constantValues().length > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param filter
	 * @return
	 */
	private boolean hasAnyDefaultValue(Filter filter) {
		if (filter.defaultValues() != null && filter.defaultValues().length > 0) {
			return true;
		}
		return false;
	}

}
