package org.mentalsmash.whadl.validation;

import org.mentalsmash.whadl.WhadlException;

public class UnknownReferenceException extends WhadlException {

	private static final long serialVersionUID = 8274211431264907691L;
	
	private final String reference;
	private final String where;
	
	public UnknownReferenceException(String reference, String where){
		super("Unknown reference \""+reference+"\" in "+where);
		this.reference = reference;
		this.where = where;
	}

	public String getReference() {
		return reference;
	}

	public String getWhere() {
		return where;
	}

}
