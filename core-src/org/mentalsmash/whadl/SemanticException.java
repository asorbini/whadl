package org.mentalsmash.whadl;

import org.mentalsmash.whadl.model.Entity;

public class SemanticException extends WhadlException {

	private static final long serialVersionUID = 4935965255568630006L;
	
	private final Exception ex;
	
	public SemanticException(Entity e,Exception ex){
		super("Semantic error in "+e,ex);
		this.ex = ex;
	}
	
	public Exception getException() {
		return ex;
	}

}
