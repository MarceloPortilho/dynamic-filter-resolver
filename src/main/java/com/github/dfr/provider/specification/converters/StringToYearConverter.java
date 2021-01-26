package com.github.dfr.provider.specification.converters;

import java.time.Year;

import org.springframework.core.convert.converter.Converter;

public class StringToYearConverter implements Converter<String, Year> {

	@Override
	public Year convert(String source) {
		return Year.parse(source);
	}

}
