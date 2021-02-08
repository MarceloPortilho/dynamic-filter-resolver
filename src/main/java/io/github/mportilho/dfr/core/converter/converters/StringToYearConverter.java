package io.github.mportilho.dfr.core.converter.converters;

import java.time.Year;
import java.time.format.DateTimeFormatter;

import io.github.mportilho.dfr.core.converter.AbstractFormattedConverter;

public class StringToYearConverter extends AbstractFormattedConverter<String, Year, String> {

	@Override
	public Year convert(String source, String format) {
		if (format == null || format.isEmpty()) {
			return Year.parse(source);
		}
		return cache(format, DateTimeFormatter::ofPattern).parse(source, Year::from);
	}

}
