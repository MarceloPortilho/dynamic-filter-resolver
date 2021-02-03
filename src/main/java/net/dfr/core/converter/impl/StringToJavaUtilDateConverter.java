package net.dfr.core.converter.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import net.dfr.core.converter.AbstractFormattedConverter;

public class StringToJavaUtilDateConverter extends AbstractFormattedConverter<String, Date, String> {

	@Override
	public Date convert(String source, String format) {
		if (format == null || format.isEmpty()) {
			LocalDateTime localDateTime = DateConverterUtils.GENERIC_DATETIME_FORMATTER_PADDING_HOURS.parse(source, LocalDateTime::from);
			Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
			return Date.from(instant);
		}
		LocalDateTime localDateTime = cache(format, DateTimeFormatter::ofPattern).parse(source, LocalDateTime::from);
		Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}

}
