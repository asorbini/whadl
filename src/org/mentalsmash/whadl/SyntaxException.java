package org.mentalsmash.whadl;

import org.mentalsmash.whadl.parser.ParseException;

public class SyntaxException extends WhadlException {
	
	private final ParseException parseException;
	
	public SyntaxException(ParseException ex) {
		super("Syntax error",ex);
		this.parseException = ex;
	}

	public ParseException getParseException() {
		return parseException;
	}

	private static final long serialVersionUID = 1978927705531343176L;

}
