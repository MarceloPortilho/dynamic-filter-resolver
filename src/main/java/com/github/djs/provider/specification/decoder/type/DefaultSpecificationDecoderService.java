package com.github.djs.provider.specification.decoder.type;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;

import com.github.djs.decoder.FilterDecoder;
import com.github.djs.decoder.type.Equals;
import com.github.djs.decoder.type.Greater;
import com.github.djs.decoder.type.GreaterOrEquals;
import com.github.djs.provider.specification.decoder.SpecificationDecoderService;

public class DefaultSpecificationDecoderService<T> implements SpecificationDecoderService<T> {

	@SuppressWarnings("rawtypes")
	private Map<Class<? extends FilterDecoder>, FilterDecoder<Specification<T>>> decoders;

	public DefaultSpecificationDecoderService() {
		decoders = new HashMap<>();
		decoders.put(Equals.class, new SpecEquals<T>());
		decoders.put(Greater.class, new SpecGreater<T>());
		decoders.put(GreaterOrEquals.class, new SpecGreaterOrEquals<T>());
	}

	@Override
	public FilterDecoder<Specification<T>> getDecoderFor(Class<? extends FilterDecoder<?>> decoder) {
		return decoders.get(decoder);
	}

}
