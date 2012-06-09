package org.mentalsmash.whadl.validation;

import org.mentalsmash.whadl.WhadlRuntimeException;

public class UnknownEntityException extends WhadlRuntimeException {

	private static final long serialVersionUID = 8720740844141123289L;
	private final String reference;
	private final EntityContext context;
	
	public UnknownEntityException(EntityContext context, String reference){
		super("Unknown entity \""+reference+"\" in context "+context);
		this.reference = reference;
		this.context = context;
	}

	public String getReference() {
		return reference;
	}

	public EntityContext getContext() {
		return context;
	}
	
	
}
