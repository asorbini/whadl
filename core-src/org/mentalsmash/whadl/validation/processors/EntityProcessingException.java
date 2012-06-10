package org.mentalsmash.whadl.validation.processors;

import org.mentalsmash.whadl.WhadlException;
import org.mentalsmash.whadl.model.Entity;

public class EntityProcessingException extends WhadlException {

	private static final long serialVersionUID = 6071166540137029030L;

	private final Entity entity;
	private final EntityProcessor<? extends Entity> processor;

	public EntityProcessingException(EntityProcessor<? extends Entity> processor,
			Entity e, String cause) {
		super("Processing Error: processor=" + processor + ", entity=" + e
				+ ", cause=" + cause);
		this.entity = e;
		this.processor = processor;
	}

	public EntityProcessingException(EntityProcessor<? extends Entity> processor,
			Entity e, Throwable cause) {
		super("Processing Error: processor=" + processor + ", entity=" + e
				+ ", cause=" + cause, cause);
		this.entity = e;
		this.processor = processor;
	}

	public Entity getEntity() {
		return entity;
	}

	public EntityProcessor<? extends Entity> getProcessor() {
		return processor;
	}
}
