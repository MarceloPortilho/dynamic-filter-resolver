package net.dfr.provider.specification.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.dfr.annotation.Disjunction;
import net.dfr.annotation.Filter;
import net.dfr.operator.type.Equals;
import net.dfr.provider.specification.interfaces.StatusEnum;

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
