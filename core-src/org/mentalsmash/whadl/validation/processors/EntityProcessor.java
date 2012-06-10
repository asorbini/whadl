package org.mentalsmash.whadl.validation.processors;

import org.mentalsmash.whadl.model.Entity;

public interface EntityProcessor<T extends Entity> {
	T process(T e) throws EntityProcessingException;
}
