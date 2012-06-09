package org.mentalsmash.whadl.model.expressions;

import org.mentalsmash.whadl.WhadlException;
import org.mentalsmash.whadl.model.expressions.Expression.Operator;

public class IllegalArgumentException extends WhadlException {

	private static final long serialVersionUID = -6539910176875885827L;
	private final Operator op;
	private final Object arg;
	private final Object expected;

	public IllegalArgumentException(Operator op, Object arg, Object expected) {
		super("Illegal argument for operator " + op + " : found " + arg + " (type: "+arg.getClass()+")"
				+ ", expecting " + expected);
		this.op = op;
		this.arg = arg;
		this.expected = expected;
	}

	public Operator getOperator() {
		return op;
	}

	public Object getArgument() {
		return arg;
	}

	public Object getExpected() {
		return expected;
	}

}
