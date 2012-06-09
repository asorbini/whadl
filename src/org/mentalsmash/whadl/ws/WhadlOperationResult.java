package org.mentalsmash.whadl.ws;

public class WhadlOperationResult {

	private final boolean success;
	private final String msg;

	public WhadlOperationResult(boolean success) {
		this.success = success;
		this.msg = "";
	}
	
	public WhadlOperationResult(boolean success, String msg) {
		this.success = success;
		this.msg = msg;
	}

	public boolean isSuccessful() {
		return success;
	}

	public String getMessage() {
		return msg;
	}

}
