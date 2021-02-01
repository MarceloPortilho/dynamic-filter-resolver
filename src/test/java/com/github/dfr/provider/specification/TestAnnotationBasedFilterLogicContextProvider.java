package com.github.dfr.provider.specification;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.github.dfr.filter.ConditionalStatement;
import com.github.dfr.filter.FilterParameter;
import com.github.dfr.filter.LogicType;
import com.github.dfr.operator.type.Equals;
import com.github.dfr.provider.AnnotationBasedConditionalStatementProvider;
import com.github.dfr.provider.specification.annotation.AnnotationContainerInterface;
import com.github.dfr.provider.specification.annotation.MethodArgumentAnnotations;
import com.github.dfr.provider.specification.interfaces.NoDeleteAndStatusOkSpecification;
import com.github.dfr.provider.specification.interfaces.NoDeleteExtendedStatusOKSpecification;
import com.github.dfr.provider.specification.interfaces.NoDeleteSpecification;
import com.github.dfr.provider.specification.interfaces.StatusEnum;

public class TestAnnotationBasedFilterLogicContextProvider {

	@Test
	public void testOneExtendedInterfaceWithOneDefaultParameter() {
		AnnotationBasedConditionalStatementProvider provider = new AnnotationBasedConditionalStatementProvider(null);
		ConditionalStatement statement = provider.createConditionalStatements(NoDeleteSpecification.class, null, Collections.emptyMap());

		assertThat(statement).isNotNull();
		assertThat(statement.getLogicType()).isEqualByComparingTo(LogicType.CONJUNCTION);
		assertThat(statement.getSubStatements()).isEmpty();
		assertThat(statement.getClauses()).isNotEmpty().hasSize(1);

		FilterParameter filterParameter = statement.getClauses().get(0);
		assertThat(filterParameter.getFormats()).isEmpty();
		assertThat(filterParameter.getOperator()).isNotNull().isEqualTo(Equals.class);
		assertThat(filterParameter.getParameters()).isNotEmpty().hasSize(1).contains("delete");
		assertThat(filterParameter.getPath()).isNotBlank().isEqualTo("deleted");
		assertThat(filterParameter.getTargetType()).isNotNull().isEqualTo(Boolean.class);
		assertThat(filterParameter.getValues()).isNotEmpty().containsOnly("false");
	}

	@Test
	public void testOneExtendedInterfaceWithOneDefaultParameterWithStringValueResolver() {
		AnnotationBasedConditionalStatementProvider provider = new AnnotationBasedConditionalStatementProvider(str -> str + "1");
		ConditionalStatement statement = provider.createConditionalStatements(NoDeleteSpecification.class, null, Collections.emptyMap());

		assertThat(statement).isNotNull();
		assertThat(statement.getLogicType()).isEqualByComparingTo(LogicType.CONJUNCTION);
		assertThat(statement.getSubStatements()).isEmpty();
		assertThat(statement.getClauses()).isNotEmpty().hasSize(1);

		FilterParameter filterParameter = statement.getClauses().get(0);
		assertThat(filterParameter.getFormats()).isEmpty();
		assertThat(filterParameter.getOperator()).isNotNull().isEqualTo(Equals.class);
		assertThat(filterParameter.getParameters()).isNotEmpty().hasSize(1).contains("delete");
		assertThat(filterParameter.getPath()).isNotBlank().isEqualTo("deleted");
		assertThat(filterParameter.getTargetType()).isNotNull().isEqualTo(Boolean.class);
		assertThat(filterParameter.getValues()).isNotEmpty().containsOnly("false1");
	}

	@Test
	public void testOneExtendedInterfaceWithTwoDefaultParameters() {
		AnnotationBasedConditionalStatementProvider provider = new AnnotationBasedConditionalStatementProvider(null);
		ConditionalStatement statement = provider.createConditionalStatements(NoDeleteAndStatusOkSpecification.class, null, Collections.emptyMap());
		FilterParameter filterParameter;

		assertThat(statement).isNotNull();
		assertThat(statement.getLogicType()).isEqualByComparingTo(LogicType.CONJUNCTION);
		assertThat(statement.getSubStatements()).isEmpty();
		assertThat(statement.getClauses()).isNotEmpty().hasSize(2);

		filterParameter = statement.getClauses().get(0);
		assertThat(filterParameter.getFormats()).isEmpty();
		assertThat(filterParameter.getOperator()).isNotNull().isEqualTo(Equals.class);
		assertThat(filterParameter.getParameters()).isNotEmpty().hasSize(1).contains("delete");
		assertThat(filterParameter.getPath()).isNotBlank().isEqualTo("deleted");
		assertThat(filterParameter.getTargetType()).isNotNull().isEqualTo(Boolean.class);
		assertThat(filterParameter.getValues()).isNotEmpty().containsOnly("false");

		filterParameter = statement.getClauses().get(1);
		assertThat(filterParameter.getFormats()).isEmpty();
		assertThat(filterParameter.getOperator()).isNotNull().isEqualTo(Equals.class);
		assertThat(filterParameter.getParameters()).isNotEmpty().hasSize(1).contains("status");
		assertThat(filterParameter.getPath()).isNotBlank().isEqualTo("status");
		assertThat(filterParameter.getTargetType()).isNotNull().isEqualTo(StatusEnum.class);
		assertThat(filterParameter.getValues()).isNotEmpty().containsOnly("OK");
	}

	@Test
	public void testOneComposedExtendedInterfaceWithOneDefaultParametersEach() {
		AnnotationBasedConditionalStatementProvider provider = new AnnotationBasedConditionalStatementProvider(null);
		ConditionalStatement statement = provider.createConditionalStatements(NoDeleteExtendedStatusOKSpecification.class, null,
				Collections.emptyMap());
		FilterParameter filterParameter;

		assertThat(statement).isNotNull();
		assertThat(statement.getLogicType()).isEqualByComparingTo(LogicType.CONJUNCTION);
		assertThat(statement.getSubStatements()).isNotEmpty().hasSize(2);
		assertThat(statement.getClauses()).isEmpty();

		ConditionalStatement stmt1 = statement.getSubStatements().get(0);
		assertThat(stmt1).isNotNull();
		assertThat(stmt1.getLogicType()).isEqualByComparingTo(LogicType.CONJUNCTION);
		assertThat(stmt1.getSubStatements()).isEmpty();
		assertThat(stmt1.getClauses()).isNotEmpty().hasSize(1);

		ConditionalStatement stmt2 = statement.getSubStatements().get(1);
		assertThat(stmt1).isNotNull();
		assertThat(stmt1.getLogicType()).isEqualByComparingTo(LogicType.CONJUNCTION);
		assertThat(stmt1.getSubStatements()).isEmpty();
		assertThat(stmt1.getClauses()).isNotEmpty().hasSize(1);

		filterParameter = stmt1.getClauses().get(0);
		assertThat(filterParameter.getFormats()).isEmpty();
		assertThat(filterParameter.getOperator()).isNotNull().isEqualTo(Equals.class);
		assertThat(filterParameter.getParameters()).isNotEmpty().hasSize(1).contains("delete");
		assertThat(filterParameter.getPath()).isNotBlank().isEqualTo("deleted");
		assertThat(filterParameter.getTargetType()).isNotNull().isEqualTo(Boolean.class);
		assertThat(filterParameter.getValues()).isNotEmpty().containsOnly("false");

		filterParameter = stmt2.getClauses().get(0);
		assertThat(filterParameter.getFormats()).isEmpty();
		assertThat(filterParameter.getOperator()).isNotNull().isEqualTo(Equals.class);
		assertThat(filterParameter.getParameters()).isNotEmpty().hasSize(1).contains("status");
		assertThat(filterParameter.getPath()).isNotBlank().isEqualTo("status");
		assertThat(filterParameter.getTargetType()).isNotNull().isEqualTo(StatusEnum.class);
		assertThat(filterParameter.getValues()).isNotEmpty().containsOnly("OK");
	}

	@Test
	public void testAnnotatedInterfaceAndAnnotatedParameter() {
		AnnotationBasedConditionalStatementProvider provider = new AnnotationBasedConditionalStatementProvider(null);
		Annotation[] methodArgumentAnnotations = MethodArgumentAnnotations.class.getAnnotations();

		ConditionalStatement statement = provider.createConditionalStatements(AnnotationContainerInterface.class, methodArgumentAnnotations,
				Collections.emptyMap());

		assertThat(statement).isNotNull();
		assertThat(statement.getLogicType()).isEqualByComparingTo(LogicType.CONJUNCTION);
		assertThat(statement.getSubStatements()).isNotEmpty().hasSize(2);
		assertThat(statement.getClauses()).isEmpty();

		ConditionalStatement stmt_0_0 = statement.getSubStatements().get(0);
		assertThat(stmt_0_0).isNotNull();
		assertThat(stmt_0_0.getLogicType()).isEqualByComparingTo(LogicType.CONJUNCTION);
		assertThat(stmt_0_0.getClauses()).isEmpty();
		assertThat(stmt_0_0.getSubStatements()).isNotEmpty().hasSize(3);

		ConditionalStatement stmt_0_1 = statement.getSubStatements().get(1);
		assertThat(stmt_0_1).isNotNull();
		assertThat(stmt_0_1.getLogicType()).isEqualByComparingTo(LogicType.DISJUNCTION);
		assertThat(stmt_0_1.getClauses()).isNotEmpty().hasSize(2);
		assertThat(stmt_0_1.getSubStatements()).isEmpty();

		ConditionalStatement stmt_0_0_0 = stmt_0_0.getSubStatements().get(0);
		assertThat(stmt_0_0_0).isNotNull();
		assertThat(stmt_0_0_0.getLogicType()).isEqualByComparingTo(LogicType.DISJUNCTION);
		assertThat(stmt_0_0_0.getClauses()).isNotEmpty().hasSize(2);
		assertThat(stmt_0_0_0.getSubStatements()).isEmpty();

		ConditionalStatement stmt_0_0_1 = stmt_0_0.getSubStatements().get(1);
		assertThat(stmt_0_0_1).isNotNull();
		assertThat(stmt_0_0_1.getLogicType()).isEqualByComparingTo(LogicType.CONJUNCTION);
		assertThat(stmt_0_0_1.getClauses()).isNotEmpty().hasSize(1);
		assertThat(stmt_0_0_1.getSubStatements()).isEmpty();

		ConditionalStatement stmt_0_0_2 = stmt_0_0.getSubStatements().get(2);
		assertThat(stmt_0_0_2).isNotNull();
		assertThat(stmt_0_0_2.getLogicType()).isEqualByComparingTo(LogicType.CONJUNCTION);
		assertThat(stmt_0_0_2.getClauses()).isNotEmpty().hasSize(1);
		assertThat(stmt_0_0_2.getSubStatements()).isEmpty();
	}

}
