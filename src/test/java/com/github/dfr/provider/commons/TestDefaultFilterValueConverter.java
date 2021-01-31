package com.github.dfr.provider.commons;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import com.github.dfr.operator.FilterValueConverter;

public class TestDefaultFilterValueConverter {

	@Test
	public void testDefaultFilterValueConverter() {
		FilterValueConverter filterValueConverter = new DefaultFilterValueConverter();

		Instant instant = filterValueConverter.convert("03-12-2011T10:15:30Z", Instant.class, null);
		assertThat(instant).isEqualTo("2011-12-03T10:15:30Z");

		Date javaSqlDate = filterValueConverter.convert("21/12/2014", Date.class, null);
		assertThat(javaSqlDate).isEqualTo("2014-12-21");

		java.util.Date javaUtilDate = filterValueConverter.convert("21/12/2014", java.util.Date.class, null);
		assertThat(javaUtilDate).isEqualTo("2014-12-21");

		LocalDate localDate = filterValueConverter.convert("21/12/2014", LocalDate.class, null);
		assertThat(localDate).isEqualTo("2014-12-21");

		LocalDateTime localDateTime = filterValueConverter.convert("21/12/2011T10:15:30Z", LocalDateTime.class, null);
		assertThat(localDateTime).isEqualTo("2011-12-21T10:15:30");

		LocalTime localTime = filterValueConverter.convert("10:15:30Z", LocalTime.class, null);
		assertThat(localTime).isEqualTo("10:15:30");

		OffsetDateTime offsetDateTime = filterValueConverter.convert("03-12-2007T10:15:30+01:00", OffsetDateTime.class, null);
		assertThat(offsetDateTime).isEqualTo("2007-12-03T10:15:30+01:00");

		OffsetTime offsetTime = filterValueConverter.convert("10:15-01:00", OffsetTime.class, null);
		assertThat(offsetTime).isEqualTo("10:15-01:00");

		Timestamp timestamp = filterValueConverter.convert("03-12-2011T10:15:30Z", Timestamp.class, null);
		assertThat(timestamp).isEqualTo(Timestamp.valueOf(LocalDateTime.of(2011, 12, 3, 10, 15, 30, 0)));

		Year year = filterValueConverter.convert("2014", Year.class, null);
		assertThat(year).isEqualTo(Year.of(2014));

		YearMonth yearMonth = filterValueConverter.convert("12/2012", YearMonth.class, null);
		assertThat(yearMonth).isEqualByComparingTo(YearMonth.of(2012, 12));

		ZonedDateTime zonedDateTime = filterValueConverter.convert("03-12-2011T10:15:30Z", ZonedDateTime.class, null);
		assertThat(zonedDateTime).isEqualTo("2011-12-03T10:15:30Z");
	}

}
