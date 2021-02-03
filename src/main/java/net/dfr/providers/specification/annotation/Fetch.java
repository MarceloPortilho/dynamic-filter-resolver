package net.dfr.providers.specification.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.persistence.criteria.JoinType;

@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
@Repeatable(Fetches.class)
public @interface Fetch {

	String[] value();

	FetchingMode fetchingMode() default FetchingMode.EXTRA_JOINS;

	JoinType joinType() default JoinType.LEFT;

}
