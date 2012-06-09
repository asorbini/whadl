package org.mentalsmash.whadl.validation;

import org.mentalsmash.whadl.WhadlException;

public class InstanceVerificationException extends WhadlException {

	private static final long serialVersionUID = -3910597905093859703L;

	public InstanceVerificationException(String msg) {
		super(msg);
	}

	public InstanceVerificationException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public InstanceVerificationException(String when, String what, String why,
			String where) {
		super("[" + when + "] " + what + " : " + why + " (at: " + where + ")");
	}

	public InstanceVerificationException(String when, String what, String why,
			String where, Throwable cause) {
		super("[" + when + "] " + what + " : " + why + " (at: " + where + ")",
				cause);
	}

}
