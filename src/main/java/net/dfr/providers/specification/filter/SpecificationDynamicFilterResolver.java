package net.dfr.providers.specification.filter;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import net.dfr.core.converter.FilterValueConverter;
import net.dfr.core.filter.AbstractDynamicFilterResolver;
import net.dfr.core.filter.FilterParameter;
import net.dfr.core.operator.FilterOperator;
import net.dfr.core.operator.FilterOperatorService;
import net.dfr.core.statement.ConditionalStatement;
import net.dfr.core.statement.LogicType;

public class SpecificationDynamicFilterResolver<T> extends AbstractDynamicFilterResolver<Specification<T>> {

	public SpecificationDynamicFilterResolver(FilterOperatorService<Specification<T>> filterOperatorService,
			FilterValueConverter filterValueConverter) {
		super(filterOperatorService, filterValueConverter);
	}

	@Override
	public Specification<T> emptyPredicate() {
		return Specification.where(null);
	}

	@Override
	public Specification<T> createPredicate(ConditionalStatement conditionalStatement) {
		Specification<T> rootSpec = null;
		for (FilterParameter clause : conditionalStatement.getClauses()) {
			FilterOperator<Specification<T>> operator = getFilterOperatorService().getOperatorFor(clause.getOperator());
			Specification<T> spec = operator.createFilter(clause, getFilterValueConverter());
			if (spec != null) {
				spec = clause.isNegate() ? Specification.not(spec) : spec;
				if (rootSpec == null) {
					rootSpec = spec;
				} else {
					rootSpec = conditionalStatement.isConjunction() ? rootSpec.and(spec) : rootSpec.or(spec);
				}
			}
		}
		return conditionalStatement.isNegate() ? Specification.not(rootSpec) : rootSpec;
	}

	@Override
	public Specification<T> postCondicionalStatementResolving(LogicType logicType, Specification<T> predicate,
			List<Specification<T>> subStatementPredicates) {
		Specification<T> currentPredicate = predicate;
		for (Specification<T> subPredicate : subStatementPredicates) {
			currentPredicate = logicType.isConjunction() ? currentPredicate.and(subPredicate) : currentPredicate.or(subPredicate);
		}
		return currentPredicate;
	}

}
