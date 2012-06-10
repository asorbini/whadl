package org.mentalsmash.whadl.model.expressions;

import org.mentalsmash.whadl.WhadlException;
import org.mentalsmash.whadl.model.expressions.Expression.Operator;

public class WrongArgumentsNumberException extends WhadlException {

	private static final long serialVersionUID = 8985485755071190655L;

	private final Operator op;
	private final int found;
	private final int expected;

	public WrongArgumentsNumberException(Operator op, int found, int expected) {
		super("Wrong number of arguments for operator " + op + ": found "
				+ found + ", expected " + expected);
		this.op = op;
		this.found = found;
		this.expected = expected;
	}

	public Operator getOperator() {
		return op;
	}

	public int getFound() {
		return found;
	}

	public int getExpected() {
		return expected;
	}

}
