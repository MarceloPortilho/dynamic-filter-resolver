package net.dfr.provider.specification.interfaces;

import org.springframework.data.jpa.domain.Specification;

import net.dfr.annotation.Conjunction;
import net.dfr.annotation.Filter;
import net.dfr.operator.type.Equals;

//@formatter:off
@Conjunction({
	@Filter(path = "deleted", parameters = "delete", operator = Equals.class, constantValues = "false", targetType = Boolean.class, negate = "true")
})//@formatter:on
public interface NoDeleteSpecificationNegatingPredicate<T> extends Specification<T> {

}
