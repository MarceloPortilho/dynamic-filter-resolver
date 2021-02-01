package com.github.dfr.provider.specification.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.dfr.annotation.Disjunction;
import com.github.dfr.annotation.Filter;
import com.github.dfr.operator.type.Equals;
import com.github.dfr.provider.specification.interfaces.StatusEnum;

@Documented
@Retention(RUNTIME)
@Target({ PARAMETER, TYPE })
//@formatter:off
@Disjunction({
	@Filter(path = "deleted", parameters = "delete", operator = Equals.class, constantValues = "true", targetType = Boolean.class),
	@Filter(path = "status", parameters = "status", operator = Equals.class, constantValues = "ERROR", targetType = StatusEnum.class)
})//@formatter:on
public @interface DeletedAndError {

}
