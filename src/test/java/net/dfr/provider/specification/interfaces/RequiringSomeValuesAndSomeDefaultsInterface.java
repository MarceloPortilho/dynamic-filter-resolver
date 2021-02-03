package net.dfr.provider.specification.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

import net.dfr.annotation.And;
import net.dfr.annotation.Disjunction;
import net.dfr.annotation.Filter;
import net.dfr.operator.type.Equals;
import net.dfr.operator.type.Greater;
import net.dfr.operator.type.GreaterOrEquals;
import net.dfr.operator.type.LessOrEquals;
import net.dfr.operator.type.NotEquals;

//@formatter:off
@Disjunction(
	value = {
		@Filter(path = "deleted", parameters = "delete", operator = Equals.class, defaultValues = "false", targetType = Boolean.class),
		@Filter(path = "name", parameters = "name", operator = NotEquals.class, targetType = String.class)
	},
	conjunctions = {
		@And({
			@Filter(path = "status", parameters = "status", operator = Equals.class, targetType = StatusEnum.class)
		}),
		@And({
			@Filter(path = "birthday", parameters = "birthday", operator = GreaterOrEquals.class, defaultValues = "12/12/2012", targetType = LocalDate.class),
			@Filter(path = "height", parameters = "height", operator = Greater.class, targetType = StatusEnum.class)
		}),
		@And({
			@Filter(path = "student", parameters = "student", operator = Equals.class, constantValues = "true", targetType = Boolean.class),
			@Filter(path = "weight", parameters = "weight", operator = LessOrEquals.class, targetType = BigDecimal.class)
		})
	}
)
//@formatter:on
public interface RequiringSomeValuesAndSomeDefaultsInterface {

}
