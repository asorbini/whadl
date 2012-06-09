package org.mentalsmash.whadl;

public class WhadlRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -6229753604609409826L;
	
	public WhadlRuntimeException(String msg){
		super(msg);
	}

	public WhadlRuntimeException() {
		super();
	}

	public WhadlRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public WhadlRuntimeException(Throwable cause) {
		super(cause);
	}
	
	

	
	
}
