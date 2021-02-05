package net.apps.tests.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;

import net.apps.apptest.TestingApplication;
import net.apps.apptest.domain.model.Person;
import net.apps.apptest.repository.PersonRepository;
import net.apps.tests.app.queries.ComparisonOperationsQueryInterface;
import net.apps.tests.app.queries.FetchingComposedPath;
import net.apps.tests.app.queries.FetchingSimplePath;
import net.apps.tests.app.queries.OtherOperationsQueryInterface;
import net.apps.tests.app.queries.StringOperationsQueryInterface;
import net.dfr.core.converter.DefaultFilterValueConverter;
import net.dfr.core.filter.DynamicFilterResolver;
import net.dfr.core.operator.FilterOperatorService;
import net.dfr.core.statement.ConditionalStatement;
import net.dfr.core.statement.ConditionalStatementProvider;
import net.dfr.core.statement.DefaultConditionalStatementProvider;
import net.dfr.providers.specification.annotation.Fetching;
import net.dfr.providers.specification.filter.SpecificationDynamicFilterResolver;
import net.dfr.providers.specification.operator.SpecificationFilterOperatorService;

@DataJpaTest
@ContextConfiguration(classes = TestingApplication.class)
public class TestAllSpecificationOperators {

	@Autowired
	private PersonRepository personRepo;

	@Test
	public void testComparisons() {
		ConditionalStatementProvider provider = new DefaultConditionalStatementProvider(null);
		ConditionalStatement statement = provider.createConditionalStatements(ComparisonOperationsQueryInterface.class, null, null);
		FilterOperatorService<Specification<?>> operatorService = new SpecificationFilterOperatorService();
		DefaultFilterValueConverter filterValueConverter = new DefaultFilterValueConverter();
		DynamicFilterResolver<Specification<?>> resolver = new SpecificationDynamicFilterResolver(operatorService, filterValueConverter);

		Specification<Person> specification = resolver.convertTo(statement);
		List<Person> list = personRepo.findAll(specification);

		assertThat(list).isEmpty();
	}

	@Test
	public void testStringComparisons() {
		ConditionalStatementProvider provider = new DefaultConditionalStatementProvider(null);
		ConditionalStatement statement = provider.createConditionalStatements(StringOperationsQueryInterface.class, null, null);
		FilterOperatorService<Specification<?>> operatorService = new SpecificationFilterOperatorService();
		DefaultFilterValueConverter filterValueConverter = new DefaultFilterValueConverter();
		DynamicFilterResolver<Specification<?>> resolver = new SpecificationDynamicFilterResolver(operatorService, filterValueConverter);

		Specification<Person> specification = resolver.convertTo(statement);
		List<Person> list = personRepo.findAll(specification);

		assertThat(list).isEmpty();
	}

	@Test
	public void testOtherComparisons() {
		ConditionalStatementProvider provider = new DefaultConditionalStatementProvider(null);
		ConditionalStatement statement = provider.createConditionalStatements(OtherOperationsQueryInterface.class, null, null);
		FilterOperatorService<Specification<?>> operatorService = new SpecificationFilterOperatorService();
		DefaultFilterValueConverter filterValueConverter = new DefaultFilterValueConverter();
		DynamicFilterResolver<Specification<?>> resolver = new SpecificationDynamicFilterResolver(operatorService, filterValueConverter);

		Specification<Person> specification = resolver.convertTo(statement);
		List<Person> list = personRepo.findAll(specification);

		assertThat(list).isEmpty();
	}

	@Test
	public void testSimpleFetch() {
		FilterOperatorService<Specification<?>> operatorService = new SpecificationFilterOperatorService();
		DefaultFilterValueConverter filterValueConverter = new DefaultFilterValueConverter();
		ConditionalStatementProvider provider = new DefaultConditionalStatementProvider(null);
		DynamicFilterResolver<Specification<?>> resolver = new SpecificationDynamicFilterResolver(operatorService, filterValueConverter);

		ConditionalStatement statement = provider.createConditionalStatements(FetchingSimplePath.class, null, null);

		Fetching[] anns = FetchingSimplePath.class.getAnnotationsByType(Fetching.class);
		Specification<Person> specification = resolver.convertTo(statement, Collections.singletonMap(Fetching.class, anns));
		List<Person> list = personRepo.findAll(specification);

		assertThat(list).isEmpty();
	}

	@Test
	public void testComposedFetch() {
		FilterOperatorService<Specification<?>> operatorService = new SpecificationFilterOperatorService();
		DefaultFilterValueConverter filterValueConverter = new DefaultFilterValueConverter();
		ConditionalStatementProvider provider = new DefaultConditionalStatementProvider(null);
		DynamicFilterResolver<Specification<?>> resolver = new SpecificationDynamicFilterResolver(operatorService, filterValueConverter);

		ConditionalStatement statement = provider.createConditionalStatements(FetchingComposedPath.class, null, null);

		Fetching[] anns = FetchingComposedPath.class.getAnnotationsByType(Fetching.class);
		Specification<Person> specification = resolver.convertTo(statement, Collections.singletonMap(Fetching.class, anns));
		List<Person> list = personRepo.findAll(specification);

		assertThat(list).isEmpty();
	}

}
