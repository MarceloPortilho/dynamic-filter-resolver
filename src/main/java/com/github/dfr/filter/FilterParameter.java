package com.github.dfr.filter;

import java.util.HashMap;
import java.util.Map;

import com.github.dfr.decoder.FilterDecoder;

public class FilterParameter {

	/**
	 * Name or path to the required attribute
	 * 
	 * <p>
	 * <b>Path</b> is the notation which the target attribute can be found from a
	 * specified root attribute, like <code>Person.addresses.streetName</code>
	 */
	private final String path;

	/**
	 * Parameters needed to be supplied by the caller, exposed as input data
	 * requirements
	 */
	private final String[] parameters;

	/**
	 * Target attribute type for convertion
	 */
	private final Class<?> targetType;

	/**
	 * Action to be used as a query filter
	 */
	private final Class<? extends FilterDecoder<?>> decoder;

	/**
	 * The provided parameter values from the caller
	 */
	private final Object[] values;

	/**
	 * Optional format pattern to assist data conversion for each parameter. It's
	 * recommended that each parameter has its own provided format for configuration
	 * clarity
	 */
	private final String[] valueFormats;

	/**
	 * The parameter's mutable state during filter resolution
	 */
	private Map<Object, Object> state = new HashMap<>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public FilterParameter(String path, String[] parameters, Class<?> targetType, Class<? extends FilterDecoder> decoder, Object[] value,
			String[] valueFormats) {
		this.path = path;
		this.parameters = parameters;
		this.targetType = targetType;
		this.decoder = (Class<? extends FilterDecoder<?>>) decoder;
		this.values = value;
		this.valueFormats = valueFormats;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public FilterParameter(String path, String parameter, Class<?> targetType, Class<? extends FilterDecoder> decoder, Object value,
			String valueFormat) {
		this.path = path;
		this.parameters = new String[] { parameter };
		this.targetType = targetType;
		this.decoder = (Class<? extends FilterDecoder<?>>) decoder;
		this.values = new Object[] { value };
		this.valueFormats = new String[] { valueFormat };
	}

	public String getPath() {
		return path;
	}

	public Class<?> getTargetType() {
		return targetType;
	}

	public Object[] getValues() {
		return values;
	}

	public String[] getParameters() {
		return parameters;
	}

	public Class<? extends FilterDecoder<?>> getDecoder() {
		return decoder;
	}

	public String[] getValueFormats() {
		return valueFormats;
	}

	public void addState(Object key, Object value) {
		state.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <V> V findState(Object key) {
		return (V) state.get(key);
	}

	@SuppressWarnings("unchecked")
	public <V> V findStateOrDefault(Object key, V defaultValue) {
		V stateValue = (V) state.get(key);
		return stateValue != null ? stateValue : defaultValue;
	}

	public Object findSingleValue() {
		if (values == null) {
			return null;
		} else if (values.length > 1) {
			throw new IllegalStateException("Cannot get single value because multiple values are present");
		}
		return values[0];
	}

}
