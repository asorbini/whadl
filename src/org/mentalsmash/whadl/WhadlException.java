package org.mentalsmash.whadl;

public class WhadlException extends Exception {

	private static final long serialVersionUID = -2095450414458937539L;
	
	public WhadlException(){
		super();
	}
	
	public WhadlException(Throwable cause){
		super(cause);
	}
	
	public WhadlException(String msg){
		super(msg);
	}
	
	public WhadlException(String msg,Throwable cause){
		super(msg,cause);
	}
}
